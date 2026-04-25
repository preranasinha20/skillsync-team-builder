package ui.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("📊 My Projects Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label placeholder = new Label("Dashboard loading...");

        root.getChildren().addAll(title, placeholder);

        return new Scene(root, 700, 500);
    }
}