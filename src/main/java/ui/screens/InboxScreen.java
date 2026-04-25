package ui.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.Main;

public class InboxScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // 🔙 BACK BUTTON
        Button backBtn = new Button("⬅ Back");
        backBtn.setOnAction(e -> Main.showDashboard());

        Label title = new Label("📥 Inbox");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 📦 SAMPLE REQUEST CARD
        VBox requestCard = new VBox(10);
        requestCard.setStyle("-fx-border-color: lightgray; -fx-padding: 10; -fx-background-color: white;");

        Label project = new Label("Project: AI Team Builder");
        Label from = new Label("From: Student_123");
        Label match = new Label("Match: 87%");

        HBox actions = new HBox(10);

        Button accept = new Button("✔ Accept");
        accept.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

        Button reject = new Button("✖ Reject");
        reject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        accept.setOnAction(e -> match.setText("Accepted ✅"));
        reject.setOnAction(e -> match.setText("Rejected ❌"));

        actions.getChildren().addAll(accept, reject);

        requestCard.getChildren().addAll(project, from, match, actions);

        root.getChildren().addAll(backBtn, title, requestCard);

        return new Scene(root, 600, 400);
    }
}