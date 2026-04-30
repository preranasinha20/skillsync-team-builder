package ui;

import dao.UserDAO;
import database.PasswordUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.User;
import ui.screens.TeacherHomeScreen;

public class LoginScreen {

    private Stage stage;

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    /** Called from Main.start() — shows the login screen on the stage. */
    public void show() {
        stage.setScene(getLoginScene());
        stage.setTitle("SkillSync — Login");
        stage.show();
    }

    /**
     * Returns the login Scene.
     * Used by TeacherHomeScreen logout button (which only has a Stage, no LoginScreen instance).
     */
    public Scene getLoginScene() {

        // ── Left panel ───────────────────────────────────────────
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

        Text desc = new Text("Find your perfect team.\nBuild something great.");
        desc.setFont(Font.font("Arial", 14));
        desc.setFill(Color.web("#aaaaaa"));
        desc.setTextAlignment(TextAlignment.CENTER);

        leftPanel.getChildren().addAll(brand, tagline, new Label(""), desc);

        // ── Right panel ──────────────────────────────────────────
        VBox rightPanel = new VBox(14);
        rightPanel.setPrefWidth(480);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(60));
        rightPanel.setStyle("-fx-background-color: #f0f2f5;");

        Text title = new Text("Welcome back");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        title.setFill(Color.web("#1a1a2e"));

        Text subtitle = new Text("Log in to your account");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setFill(Color.web("#6c757d"));

        TextField emailField = new TextField();
        emailField.setPromptText("Email address");
        styleField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.web("#e94560"));
        errorLabel.setFont(Font.font("Arial", 12));

        Button loginBtn = new Button("Log In");
        stylePrimaryButton(loginBtn);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            User user = UserDAO.loginUser(email, PasswordUtil.hash(password));

            if (user == null) {
                errorLabel.setText("Invalid email or password.");
                return;
            }

            SessionManager.setUser(user);

            if ("TEACHER".equalsIgnoreCase(user.getRole())) {
                stage.setScene(new TeacherHomeScreen().getScene());
            } else {
                new HomeFeedScreen(stage).show();
            }
        });

        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setFont(Font.font("Arial", 12));
        registerLink.setTextFill(Color.web("#1a1a2e"));
        registerLink.setOnAction(e -> new RegisterScreen(stage).show());

        rightPanel.getChildren().addAll(
            title, subtitle,
            new Label(""),
            fieldLabel("Email"), emailField,
            fieldLabel("Password"), passwordField,
            errorLabel,
            loginBtn,
            registerLink
        );

        HBox root = new HBox(leftPanel, rightPanel);
        return new Scene(root, 900, 600);
    }

    // ── Style helpers ────────────────────────────────────────────
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