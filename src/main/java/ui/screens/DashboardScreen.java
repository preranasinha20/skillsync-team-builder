package ui.screens;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.Main;

public class DashboardScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("📊 My Projects Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // NAVIGATION BUTTONS
        HBox nav = new HBox(10);

        Button postBtn = new Button("➕ Post Project");
        Button inboxBtn = new Button("📥 Inbox");

        postBtn.setOnAction(e -> Main.showPostProject());
        inboxBtn.setOnAction(e -> Main.showInbox());

        nav.getChildren().addAll(postBtn, inboxBtn);

        // TABS
        TabPane tabPane = new TabPane();

        Tab posted = new Tab("Posted");
        Tab joined = new Tab("Joined");
        Tab completed = new Tab("Completed");

        posted.setClosable(false);
        joined.setClosable(false);
        completed.setClosable(false);

        tabPane.getTabs().addAll(posted, joined, completed);

        root.getChildren().addAll(title, nav, tabPane);

        return new Scene(root, 700, 500);
    }
}