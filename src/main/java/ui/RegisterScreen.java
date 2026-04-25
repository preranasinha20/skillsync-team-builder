package ui;

import dao.UserDAO;
import database.PasswordUtil;
import model.Student;
import model.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class RegisterScreen {

    private Stage stage;

    public RegisterScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        // ── Left panel ──────────────────────────────────────────
        VBox leftPanel = new VBox(12);
        leftPanel.setPrefWidth(420);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setStyle("-fx-background-color: #1a1a2e;");
        leftPanel.setPadding(new Insets(60));

        Text brand = new Text("SkillSync");
        brand.setFont(Font.font("Georgia", FontWeight.BOLD, 42));
        brand.setFill(Color.WHITE);

        Text tagline = new Text("Smart Team Builder");
        tagline.setFont(Font.font("Georgia", FontPosture.ITALIC, 16));
        tagline.setFill(Color.web("#e94560"));

        Text desc = new Text("Join thousands of students\nbuilding great projects together.");
        desc.setFont(Font.font("Arial", 14));
        desc.setFill(Color.web("#aaaaaa"));
        desc.setTextAlignment(TextAlignment.CENTER);

        leftPanel.getChildren().addAll(brand, tagline, new Label(""), desc);

        // ── Right panel ─────────────────────────────────────────
        VBox rightPanel = new VBox(10);
        rightPanel.setPrefWidth(480);
        rightPanel.setAlignment(Pos.CENTER_LEFT);
        rightPanel.setPadding(new Insets(50, 60, 50, 60));
        rightPanel.setStyle("-fx-background-color: #f0f2f5;");

        Text title = new Text("Create Account");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web("#1a1a2e"));

        Text subtitle = new Text("Fill in your details to get started");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setFill(Color.web("#6c757d"));

        // ── Fields ──────────────────────────────────────────────
        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        styleField(nameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email address");
        styleField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);

        TextField branchField = new TextField();
        branchField.setPromptText("e.g. AIML, CSE, ECE");
        styleField(branchField);

        TextField batchField = new TextField();
        batchField.setPromptText("e.g. 2024");
        styleField(batchField);

        // ── Role toggle ─────────────────────────────────────────
        ToggleGroup roleGroup = new ToggleGroup();
        RadioButton studentBtn = new RadioButton("Student");
        RadioButton teacherBtn = new RadioButton("Teacher");
        studentBtn.setToggleGroup(roleGroup);
        teacherBtn.setToggleGroup(roleGroup);
        studentBtn.setSelected(true);
        studentBtn.setFont(Font.font("Arial", 13));
        teacherBtn.setFont(Font.font("Arial", 13));
        HBox roleBox = new HBox(20, studentBtn, teacherBtn);

        // Department field — only visible for teachers
        TextField departmentField = new TextField();
        departmentField.setPromptText("Department e.g. Computer Science");
        styleField(departmentField);
        departmentField.setVisible(false);
        departmentField.setManaged(false);

        Label deptLabel = fieldLabel("Department");
        deptLabel.setVisible(false);
        deptLabel.setManaged(false);

        teacherBtn.setOnAction(e -> {
            departmentField.setVisible(true);
            departmentField.setManaged(true);
            deptLabel.setVisible(true);
            deptLabel.setManaged(true);
        });
        studentBtn.setOnAction(e -> {
            departmentField.setVisible(false);
            departmentField.setManaged(false);
            deptLabel.setVisible(false);
            deptLabel.setManaged(false);
        });

        // ── Error / success label ────────────────────────────────
        Label messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", 12));

        // ── Register button ──────────────────────────────────────
        Button registerBtn = new Button("Create Account");
        stylePrimaryButton(registerBtn);

        registerBtn.setOnAction(e -> {
            String name       = nameField.getText().trim();
            String email      = emailField.getText().trim();
            String password   = passwordField.getText();
            String branch     = branchField.getText().trim();
            String batchStr   = batchField.getText().trim();
            boolean isTeacher = teacherBtn.isSelected();
            String department = departmentField.getText().trim();

            // Validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()
                    || branch.isEmpty() || batchStr.isEmpty()) {
                messageLabel.setTextFill(Color.web("#e94560"));
                messageLabel.setText("Please fill in all fields.");
                return;
            }

            int batch;
            try {
                batch = Integer.parseInt(batchStr);
            } catch (NumberFormatException ex) {
                messageLabel.setTextFill(Color.web("#e94560"));
                messageLabel.setText("Batch must be a year e.g. 2024");
                return;
            }

            if (UserDAO.emailExists(email)) {
                messageLabel.setTextFill(Color.web("#e94560"));
                messageLabel.setText("An account with this email already exists.");
                return;
            }

            String hash = PasswordUtil.hash(password);
            boolean success;

            if (isTeacher) {
                if (department.isEmpty()) {
                    messageLabel.setTextFill(Color.web("#e94560"));
                    messageLabel.setText("Please enter your department.");
                    return;
                }
                Teacher teacher = new Teacher(name, email, hash, branch, batch, department);
                success = UserDAO.registerUser(teacher);
            } else {
                Student student = new Student(name, email, hash, branch, batch);
                success = UserDAO.registerUser(student);
            }

            if (success) {
                messageLabel.setTextFill(Color.web("#2ecc71"));
                messageLabel.setText("Account created! Redirecting to login...");
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(1.5)
                );
                pause.setOnFinished(ev -> new LoginScreen(stage).show());
                pause.play();
            } else {
                messageLabel.setTextFill(Color.web("#e94560"));
                messageLabel.setText("Registration failed. Please try again.");
            }
        });

        // ── Back to login ────────────────────────────────────────
        Hyperlink loginLink = new Hyperlink("Already have an account? Log in");
        loginLink.setFont(Font.font("Arial", 12));
        loginLink.setTextFill(Color.web("#1a1a2e"));
        loginLink.setOnAction(e -> new LoginScreen(stage).show());

        rightPanel.getChildren().addAll(
            title, subtitle,
            new Label(""),
            fieldLabel("Full Name"), nameField,
            fieldLabel("Email"), emailField,
            fieldLabel("Password"), passwordField,
            fieldLabel("Branch"), branchField,
            fieldLabel("Batch Year"), batchField,
            fieldLabel("Role"), roleBox,
            deptLabel, departmentField,
            messageLabel,
            registerBtn,
            loginLink
        );

        // ── Scroll in case screen is small ───────────────────────
        ScrollPane scroll = new ScrollPane(rightPanel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f0f2f5; -fx-background: #f0f2f5;");

        HBox root = new HBox(leftPanel, scroll);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("SkillSync — Register");
        stage.setScene(scene);
        stage.show();
    }

    private Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#333333"));
        return lbl;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(42);
        field.setPrefWidth(340);
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 13px;"
        );
    }

    private void stylePrimaryButton(Button btn) {
        btn.setPrefWidth(340);
        btn.setPrefHeight(44);
        btn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #c73652;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        ));
    }
}