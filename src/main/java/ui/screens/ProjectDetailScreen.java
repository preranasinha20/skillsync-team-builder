package ui.screens;

import dao.TeamRequestDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class ProjectDetailScreen {

    private Stage stage;
    private Project project;

    public ProjectDetailScreen(Stage stage, Project project) {
        this.stage = stage;
        this.project = project;
    }

    public void show() {

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f4f6f9;");

        // 🔹 NAVBAR
        HBox navbar = new HBox();
        navbar.setPadding(new Insets(16, 28, 16, 28));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #1a1a2e;");

        Text title = new Text("Project Details");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button back = new Button("← Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #dcdcdc;");
        back.setOnAction(e -> new HomeFeedScreen(stage).show());

        navbar.getChildren().addAll(title, spacer, back);

        // 🔹 CARD
        VBox card = new VBox(16);
        card.setPadding(new Insets(30));
        card.setMaxWidth(600);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 6);"
        );

        Text name = new Text(project.getTitle());
        name.setFont(Font.font("Georgia", FontWeight.BOLD, 22));

        Text desc = new Text(project.getDescription());
        desc.setFill(Color.web("#555"));
        desc.setWrappingWidth(500);

        HBox tags = new HBox(10);
        tags.getChildren().addAll(
                chip(project.getBranch()),
                chip(String.valueOf(project.getBatch())),
                chip("Team: " + project.getTeamSize())
        );

        Label status = new Label("Status: " + project.getStatusString());
        status.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");

        Button apply = new Button("Request to Join");
        apply.setPrefHeight(40);
        apply.setStyle(
            "-fx-background-color: linear-gradient(to right, #e94560, #ff6b81);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;"
        );

        Label feedback = new Label();

        apply.setOnAction(e -> {
            try {
                int sender = SessionManager.getUser().getId();
                int receiver = project.getOwnerId();

                boolean ok = TeamRequestDAO.sendRequest(
                        project.getId(),
                        sender,
                        receiver,
                        "JOIN",
                        "Wants to join"
                );

                if (ok) {
                    feedback.setText("Request sent ✔");
                    feedback.setStyle("-fx-text-fill: green;");
                } else {
                    feedback.setText("Already requested");
                    feedback.setStyle("-fx-text-fill: orange;");
                }

            } catch (Exception ex) {
                feedback.setText("Error");
                feedback.setStyle("-fx-text-fill: red;");
            }
        });

        card.getChildren().addAll(
                name,
                desc,
                tags,
                status,
                apply,
                feedback
        );

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(50));

        root.getChildren().addAll(navbar, center);

        stage.setScene(new Scene(root, 920, 620));
    }

    private Label chip(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-background-color: #eef1f6;" +
            "-fx-padding: 5 12;" +
            "-fx-background-radius: 8;"
        );
        return lbl;
    }
}