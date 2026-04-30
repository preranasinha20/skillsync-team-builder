package ui.screens;

import java.util.Arrays;

import dao.ProjectDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Project;
import ui.HomeFeedScreen;
import ui.SessionManager;

public class PostProjectScreen {

    private Stage stage;

    public PostProjectScreen(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f4f6f9;");

        // 🔹 NAVBAR
        HBox navbar = new HBox();
        navbar.setPadding(new Insets(16, 28, 16, 28));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #1a1a2e;");

        Text title = new Text("Create Project");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button back = new Button("← Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #dcdcdc;");
        back.setOnAction(e -> new HomeFeedScreen(stage).show());

        navbar.getChildren().addAll(title, spacer, back);

        // 🔹 CARD
        VBox card = new VBox(18);
        card.setPadding(new Insets(30));
        card.setMaxWidth(550);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 6);"
        );

        Text heading = new Text("✨ Start a new project");
        heading.setFont(Font.font("Georgia", FontWeight.BOLD, 18));

        Text sub = new Text("Define your idea, required skills and team size");
        sub.setFill(Color.web("#6c757d"));

        TextField titleField = input("Project Title");

        TextArea desc = new TextArea();
        desc.setPromptText("Describe your project clearly...");
        desc.setPrefHeight(120);
        style(desc);

        TextField skills = input("Skills (Java, ML, UI...)");

        Spinner<Integer> teamSize = new Spinner<>(1, 10, 3);
        teamSize.setPrefHeight(38);

        Label teamLabel = new Label("Team Size");
        teamLabel.setStyle("-fx-font-weight: bold;");

        Button submit = new Button("🚀 Post Project");
        submit.setPrefHeight(42);
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setStyle(
            "-fx-background-color: linear-gradient(to right, #e94560, #ff6b81);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;"
        );

        Label status = new Label();

        // LOGIC
        submit.setOnAction(e -> {

            if (titleField.getText().isEmpty() || desc.getText().isEmpty()) {
                status.setStyle("-fx-text-fill: red;");
                status.setText("Please fill all required fields");
                return;
            }

            try {
                int userId = SessionManager.getUser().getId();

                Project project = new Project(
                        userId,
                        titleField.getText(),
                        desc.getText(),
                        teamSize.getValue(),
                        2024,
                        "AIML"
                );

                int id = ProjectDAO.createProject(project);

                if (id != -1) {
                    if (!skills.getText().isEmpty()) {
                        ProjectDAO.addProjectSkills(id,
                                Arrays.asList(skills.getText().split(",")));
                    }

                    status.setStyle("-fx-text-fill: green;");
                    status.setText("Project created successfully ✔");

                    new HomeFeedScreen(stage).show();
                }

            } catch (Exception ex) {
                status.setStyle("-fx-text-fill: red;");
                status.setText("Error: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(
                heading, sub,
                titleField,
                desc,
                skills,
                teamLabel,
                teamSize,
                submit,
                status
        );

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(50));

        root.getChildren().addAll(navbar, center);

        return new Scene(root, 920, 620);
    }

    private TextField input(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(38);
        style(tf);
        return tf;
    }

    private void style(Control c) {
        c.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 12;"
        );
    }
}