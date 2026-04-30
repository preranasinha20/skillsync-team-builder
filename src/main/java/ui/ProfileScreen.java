package ui;

import dao.UserDAO;
import model.Skill;
import model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.List;

public class ProfileScreen {

    private Stage stage;
    private User currentUser;

    public ProfileScreen(Stage stage) {
        this.stage = stage;
        this.currentUser = SessionManager.getUser();
    }

    public void show() {

        // ── Navbar ───────────────────────────────────────────────
        HBox navbar = new HBox();
        navbar.setPadding(new Insets(0, 24, 0, 24));
        navbar.setPrefHeight(56);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #1a1a2e;");

        Text logo = new Text("SkillSync");
        logo.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        logo.setFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = navButton("← Home");
        Button logoutBtn = navButton("Logout");

        backBtn.setOnAction(e -> new HomeFeedScreen(stage).show());
        logoutBtn.setOnAction(e -> {
            SessionManager.clear();
            new LoginScreen(stage).show();
        });

        navbar.getChildren().addAll(logo, spacer, backBtn,
                new Label("   "), logoutBtn);

        // ── Profile header card ──────────────────────────────────
        VBox headerCard = new VBox(8);
        headerCard.setPadding(new Insets(24));
        headerCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Text nameText = new Text(currentUser.getName());
        nameText.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        nameText.setFill(Color.web("#1a1a2e"));

        HBox metaRow = new HBox(16);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        metaRow.getChildren().addAll(
            metaTag(currentUser.getEmail()),
            metaTag(currentUser.getBranch()),
            metaTag(String.valueOf(currentUser.getBatch())),
            metaTag(currentUser.getRole())
        );

        // Bio section
        Label bioLabel = new Label("Bio");
        bioLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        bioLabel.setTextFill(Color.web("#333333"));

        TextField bioField = new TextField(
            currentUser.getBio() != null ? currentUser.getBio() : ""
        );
        bioField.setPromptText("Tell others about yourself...");
        bioField.setPrefWidth(500);
        styleField(bioField);

        Button saveBioBtn = new Button("Save Bio");
        styleAccentButton(saveBioBtn);
        saveBioBtn.setOnAction(e -> {
            currentUser.setBio(bioField.getText().trim());
            boolean saved = UserDAO.updateProfile(currentUser);
            if (saved) {
                showAlert("Bio updated successfully!");
            } else {
                showAlert("Failed to update bio.");
            }
        });

        HBox bioRow = new HBox(10, bioField, saveBioBtn);
        bioRow.setAlignment(Pos.CENTER_LEFT);

        headerCard.getChildren().addAll(nameText, metaRow,
                new Label(""), bioLabel, bioRow);

        // ── Skills section ───────────────────────────────────────
        VBox skillsSection = new VBox(16);
        skillsSection.setPadding(new Insets(24));

        Text skillsTitle = new Text("My Skills");
        skillsTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        skillsTitle.setFill(Color.web("#1a1a2e"));

        // Skills list container
        VBox skillsList = new VBox(10);
        loadSkills(skillsList);

        // Add skill form
        HBox addSkillForm = new HBox(10);
        addSkillForm.setAlignment(Pos.CENTER_LEFT);
        addSkillForm.setPadding(new Insets(16, 0, 0, 0));

        TextField skillNameField = new TextField();
        skillNameField.setPromptText("Skill name e.g. Java");
        skillNameField.setPrefWidth(200);
        styleField(skillNameField);

        ComboBox<String> proficiencyBox = new ComboBox<>();
        proficiencyBox.getItems().addAll("BEGINNER", "INTERMEDIATE", "EXPERT");
        proficiencyBox.setValue("BEGINNER");
        proficiencyBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 13px;"
        );
        proficiencyBox.setPrefHeight(42);

        Button addSkillBtn = new Button("+ Add Skill");
        stylePrimaryButton(addSkillBtn);

        addSkillBtn.setOnAction(e -> {
            String skillName = skillNameField.getText().trim();
            String proficiency = proficiencyBox.getValue();

            if (skillName.isEmpty()) {
                showAlert("Please enter a skill name.");
                return;
            }

            boolean added = UserDAO.addSkill(
                currentUser.getId(), skillName, proficiency
            );

            if (added) {
                skillNameField.clear();
                proficiencyBox.setValue("BEGINNER");
                loadSkills(skillsList);
            } else {
                showAlert("Failed to add skill.");
            }
        });

        addSkillForm.getChildren().addAll(
            skillNameField, proficiencyBox, addSkillBtn
        );

        skillsSection.getChildren().addAll(
            skillsTitle, skillsList, addSkillForm
        );

        // ── Scroll wrapper ───────────────────────────────────────
        VBox content = new VBox(headerCard, skillsSection);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background-color: #f0f2f5;" +
            "-fx-background: #f0f2f5;"
        );

        VBox root = new VBox(navbar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("SkillSync — My Profile");
        stage.setScene(scene);
        stage.show();
    }

    // ── Load skills into list ────────────────────────────────────
    private void loadSkills(VBox container) {
        container.getChildren().clear();
        List<Skill> skills = UserDAO.getSkillsByUser(currentUser.getId());

        if (skills.isEmpty()) {
            Label empty = new Label("No skills added yet. Add your first skill below!");
            empty.setFont(Font.font("Arial", 13));
            empty.setTextFill(Color.web("#6c757d"));
            container.getChildren().add(empty);
            return;
        }

        for (Skill skill : skills) {
            container.getChildren().add(buildSkillRow(skill, container));
        }
    }

    // ── Skill row ────────────────────────────────────────────────
    private HBox buildSkillRow(Skill skill, VBox container) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 1);"
        );

        Label skillName = new Label(skill.getSkillName());
        skillName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        skillName.setTextFill(Color.web("#1a1a2e"));
        skillName.setPrefWidth(200);

        // Proficiency badge
        Label profBadge = new Label(skill.getProficiency());
        String badgeColor = switch (skill.getProficiency()) {
            case "EXPERT"       -> "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32;";
            case "INTERMEDIATE" -> "-fx-background-color: #fff8e1; -fx-text-fill: #f57f17;";
            default             -> "-fx-background-color: #e3f2fd; -fx-text-fill: #1565c0;";
        };
        profBadge.setStyle(
            badgeColor +
            "-fx-padding: 3 12;" +
            "-fx-background-radius: 99;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );

        Region rowSpacer = new Region();
        HBox.setHgrow(rowSpacer, Priority.ALWAYS);

        // Delete button
        Button deleteBtn = new Button("✕");
        deleteBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #e94560;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #e94560;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 2 8;"
        );
        deleteBtn.setOnAction(e -> {
            boolean deleted = UserDAO.deleteSkill(skill.getId());
            if (deleted) {
                loadSkills(container);
            } else {
                showAlert("Failed to delete skill.");
            }
        });

        row.getChildren().addAll(skillName, profBadge, rowSpacer, deleteBtn);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────
    private Label metaTag(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setTextFill(Color.web("#6c757d"));
        lbl.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-padding: 3 10;" +
            "-fx-background-radius: 4;"
        );
        return lbl;
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #aaaaaa;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #aaaaaa;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(42);
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
        btn.setPrefHeight(42);
        btn.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
    }

    private void styleAccentButton(Button btn) {
        btn.setPrefHeight(42);
        btn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SkillSync");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}