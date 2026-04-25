package ui.screens;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PostProjectScreen {

    public Scene getScene() {

        VBox root = new VBox(10);

        Label title = new Label("📝 Post New Project");
        Label placeholder = new Label("Project form (Coming Soon)");

        root.getChildren().addAll(title, placeholder);

        return new Scene(root, 600, 400);
    }
}