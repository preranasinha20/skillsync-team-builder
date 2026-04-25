package ui.screens;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardScreen {

    public Scene getScene() {

        VBox root = new VBox(10);

        Label title = new Label("📊 My Projects Dashboard");
        Label placeholder = new Label("Projects view (Coming Soon)");

        root.getChildren().addAll(title, placeholder);

        return new Scene(root, 600, 400);
    }
}