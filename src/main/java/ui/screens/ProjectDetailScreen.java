package ui.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Project;
import ui.Main;

public class ProjectDetailScreen {

    private Stage stage;
    private Project project;

    public ProjectDetailScreen(Stage stage, Project project) {
        this.stage = stage;
        this.project = project;
    }

    public void show() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // 🔹 Back button
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1a1a2e;");
        backBtn.setOnAction(e -> Main.showDashboard());

        // 🔹 Card container
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #dee2e6;"
        );

        // 🔹 Title
        Label title = new Label(project.getTitle());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

        // 🔹 Description
        Label desc = new Label(project.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #6c757d;");

        // 🔹 Meta info (clean spacing)
        Label branch = meta("Branch: " + project.getBranch());
        Label batch = meta("Batch: " + project.getBatch());
        Label team = meta("Team Size: " + project.getTeamSize());
        Label status = meta("Status: " + project.getStatusString());

        // 🔹 Apply button
        Button applyBtn = new Button("Request to Join");
        applyBtn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 6;"
        );
        

        Label actionStatus = new Label();

applyBtn.setOnAction(e -> {
    actionStatus.setText("Request sent successfully ✔");
});

        // 🔹 Assemble card
        card.getChildren().addAll(
                title,
                desc,
                branch,
                batch,
                team,
                status,
                applyBtn,
                actionStatus
        );

        root.getChildren().addAll(backBtn, card);

        stage.setScene(new Scene(root, 650, 450));
    }

    // 🔹 helper for consistent text styling
    private Label meta(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #495057;");
        return lbl;
    }
}
