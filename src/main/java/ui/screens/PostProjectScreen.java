package ui.screens;

import java.util.Arrays;
import java.util.List;

import dao.ProjectDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Project;
import ui.Main;
import ui.SessionManager;

public class PostProjectScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // 🔹 Title
        Label title = new Label("📝 Post New Project");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

        // 🔹 Inputs
        TextField projectTitle = styledField("Project Title");

        TextArea description = new TextArea();
        description.setPromptText("Project Description");
        description.setPrefHeight(120);
        styleField(description);

        TextField skills = styledField("Required Skills (comma separated)");

        Spinner<Integer> teamSize = new Spinner<>(1, 10, 3);

        // 🔹 Button
        Button submitBtn = new Button("Post Project");
        submitBtn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 6;"
        );

        Label status = new Label();

        // 🔥 REAL LOGIC (DB CONNECTED)
        submitBtn.setOnAction(e -> {

            String titleText = projectTitle.getText().trim();
            String descText = description.getText().trim();
            String skillsInput = skills.getText().trim();
            int size = teamSize.getValue();

            // 🔸 Validation
            if (titleText.isEmpty() || descText.isEmpty()) {
                status.setStyle("-fx-text-fill: red;");
                status.setText("Please fill all required fields");
                return;
            }

            try {
                int userId = SessionManager.getUser().getId();

                Project project = new Project(
                        userId,
                        titleText,
                        descText,
                        size,
                        2024,     // can be dynamic later
                        "AIML"    // can be dynamic later
                );

                int projectId = ProjectDAO.createProject(project);

                if (projectId != -1) {

                    // 🔹 Save skills
                    if (!skillsInput.isEmpty()) {
                        List<String> skillList = Arrays.asList(skillsInput.split(","));
                        ProjectDAO.addProjectSkills(projectId, skillList);
                    }

                    status.setStyle("-fx-text-fill: #2ecc71;");
                    status.setText("Project posted successfully ✔");

                    // 🔥 Redirect to dashboard
                    Main.showDashboard();

                } else {
                    status.setStyle("-fx-text-fill: red;");
                    status.setText("Failed to create project");
                }

            } catch (Exception ex) {
                status.setStyle("-fx-text-fill: red;");
                status.setText("Error: " + ex.getMessage());
            }
        });

        // 🔹 Layout
        root.getChildren().addAll(
                title,
                projectTitle,
                description,
                skills,
                new Label("Team Size"),
                teamSize,
                submitBtn,
                status
        );

        return new Scene(root, 600, 500);
    }

    // 🔹 Styled input field
    private TextField styledField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        styleField(field);
        return field;
    }

    // 🔹 Common style
    private void styleField(Control field) {
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 10;"
        );
    }
}