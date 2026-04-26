package ui.screens;

import java.util.List;

import dao.ProjectDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Project;
import ui.Main;
import ui.SessionManager;

public class DashboardScreen {

    public Scene getScene() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // 🔹 HEADER
        Label title = new Label("📊 My Projects Dashboard");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

        // 🔹 NAV BAR
        HBox nav = new HBox(15);
        nav.setAlignment(Pos.CENTER_LEFT);

        Button postBtn = new Button("➕ Post Project");
        Button inboxBtn = new Button("📥 Inbox");

        postBtn.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: white;");
        inboxBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white;");

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

        // 🔥 FETCH USER PROJECTS (REAL DB)
        VBox postedBox = new VBox(10);
        postedBox.setPadding(new Insets(10));

        int userId = SessionManager.getUser().getId();

        List<Project> myProjects = ProjectDAO.getProjectsByOwner(userId);

        if (myProjects.isEmpty()) {
            Label empty = new Label("📌 No projects posted yet");
            empty.setStyle("-fx-text-fill: #6c757d;");
            postedBox.getChildren().add(empty);
        } else {
            for (Project p : myProjects) {
                postedBox.getChildren().add(buildCard(p));
            }
        }

        ScrollPane scroll = new ScrollPane(postedBox);
        scroll.setFitToWidth(true);

        posted.setContent(scroll);

        // 🔹 placeholders (can upgrade later)
        joined.setContent(new Label("🤝 Joined projects coming soon"));
        completed.setContent(new Label("✅ Completed projects coming soon"));

        tabPane.getTabs().addAll(posted, joined, completed);

        VBox.setVgrow(tabPane, Priority.ALWAYS);

        root.getChildren().addAll(title, nav, tabPane);

        return new Scene(root, 800, 550);
    }

    // 🔥 PROJECT CARD (clean UI like HomeFeed)
    private VBox buildCard(Project project) {

        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 8;"
        );

        Text title = new Text(project.getTitle());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label desc = new Label(project.getDescription());
        desc.setWrapText(true);

        Label meta = new Label(
                "Team: " + project.getTeamSize() +
                " | " + project.getBranch() +
                " | Batch: " + project.getBatch()
        );
        meta.setStyle("-fx-text-fill: #6c757d;");

        card.getChildren().addAll(title, desc, meta);

        return card;
    }
}