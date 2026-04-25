package ui.screens;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InboxScreen {

    public Scene getScene() {

        VBox root = new VBox(10);

        Label title = new Label("📥 Inbox");
        Label placeholder = new Label("Requests will appear here (Coming Soon)");

        root.getChildren().addAll(title, placeholder);

        return new Scene(root, 600, 400);
    }
}