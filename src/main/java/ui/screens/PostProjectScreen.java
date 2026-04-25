package ui.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PostProjectScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("📝 Post New Project");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField projectTitle = new TextField();
        projectTitle.setPromptText("Project Title");

        TextArea description = new TextArea();
        description.setPromptText("Project Description");
        description.setPrefHeight(100);

        TextField skills = new TextField();
        skills.setPromptText("Required Skills (comma separated)");

        Spinner<Integer> teamSize = new Spinner<>(1, 10, 3);

        Button submitBtn = new Button("Post Project");
        submitBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Label status = new Label();

        submitBtn.setOnAction(e -> {
            // Placeholder logic
            status.setText("Project posted (UI only - backend pending)");
        });

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

        return new Scene(root, 600, 450);
    }
}