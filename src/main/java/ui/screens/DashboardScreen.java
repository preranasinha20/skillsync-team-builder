package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.Main;

public class DashboardScreen {

    public Scene getScene() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        // 🔹 HEADER
        Label title = new Label("📊 My Projects Dashboard");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

        // 🔹 NAV BAR
        HBox nav = new HBox(15);
        nav.setAlignment(Pos.CENTER_LEFT);

        Button postBtn = new Button("➕ Post Project");
        Button inboxBtn = new Button("📥 Inbox");

        // 🎨 Styled buttons (your theme)
        postBtn.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: white; -fx-background-radius: 6;");
        inboxBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-background-radius: 6;");

        // 🔁 Navigation
        postBtn.setOnAction(e -> Main.showPostProject());
        inboxBtn.setOnAction(e -> Main.showInbox());

        nav.getChildren().addAll(postBtn, inboxBtn);

        // 🔹 TABS
        TabPane tabPane = new TabPane();

        Tab posted = new Tab("Posted");
        Tab joined = new Tab("Joined");
        Tab completed = new Tab("Completed");

        posted.setClosable(false);
        joined.setClosable(false);
        completed.setClosable(false);

        // 📌 Placeholder content (so tabs don’t look empty)
        posted.setContent(new Label("Your posted projects will appear here"));
        joined.setContent(new Label("Projects you joined will appear here"));
        completed.setContent(new Label("Completed projects will appear here"));

        tabPane.getTabs().addAll(posted, joined, completed);

        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // 🔹 FINAL LAYOUT
        root.getChildren().addAll(title, nav, tabPane);

        return new Scene(root, 700, 500);
    }
}