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
import dao.EventDAO;
import model.Event;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;

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

        Button backBtn = new Button("← Home");
        backBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 6;");
        backBtn.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            new ui.HomeFeedScreen(stage).show();
        });
        nav.getChildren().addAll(backBtn, postBtn, inboxBtn);

        

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

        // ── Joined tab ───────────────────────────────────────────
        VBox joinedBox = new VBox(10);
        joinedBox.setPadding(new Insets(10));
        Label joinedLoading = new Label("Loading...");
        joinedLoading.setStyle("-fx-text-fill: #6c757d;");
        joinedBox.getChildren().add(joinedLoading);

        javafx.concurrent.Task<List<Project>> joinedTask = new javafx.concurrent.Task<>() {
            @Override protected List<Project> call() {
                // Get all projects where user is a team member but not the owner
                String sql = "SELECT p.* FROM projects p " +
                            "JOIN team_members tm ON p.id = tm.project_id " +
                            "WHERE tm.user_id = ? AND p.owner_id != ?";
                List<Project> result = new java.util.ArrayList<>();
                try (java.sql.PreparedStatement stmt =
                        database.DBConnection.getConnection().prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, userId);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    while (rs.next()) result.add(dao.ProjectDAO.getProjectById(rs.getInt("id")));
                } catch (Exception e) {
                    System.err.println("Joined projects load failed: " + e.getMessage());
                }
                return result;
            }
        };
        joinedTask.setOnSucceeded(ev -> javafx.application.Platform.runLater(() -> {
            joinedBox.getChildren().clear();
            List<Project> joinedProjects = joinedTask.getValue();
            if (joinedProjects.isEmpty()) {
                Label empty = new Label("🤝 You haven't joined any projects yet");
                empty.setStyle("-fx-text-fill: #6c757d;");
                joinedBox.getChildren().add(empty);
            } else {
                for (Project p : joinedProjects) joinedBox.getChildren().add(buildCard(p));
            }
        }));
        new Thread(joinedTask).start();
        ScrollPane joinedScroll = new ScrollPane(joinedBox);
        joinedScroll.setFitToWidth(true);
        joined.setContent(joinedScroll);

        // ── Completed tab ─────────────────────────────────────────
        VBox completedBox = new VBox(10);
        completedBox.setPadding(new Insets(10));
        Label completedLoading = new Label("Loading...");
        completedLoading.setStyle("-fx-text-fill: #6c757d;");
        completedBox.getChildren().add(completedLoading);

        javafx.concurrent.Task<List<Project>> completedTask = new javafx.concurrent.Task<>() {
            @Override protected List<Project> call() {
                String sql = "SELECT p.* FROM projects p " +
                            "JOIN team_members tm ON p.id = tm.project_id " +
                            "WHERE tm.user_id = ? AND p.status = 'COMPLETED'";
                List<Project> result = new java.util.ArrayList<>();
                try (java.sql.PreparedStatement stmt =
                        database.DBConnection.getConnection().prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    java.sql.ResultSet rs = stmt.executeQuery();
                    while (rs.next()) result.add(dao.ProjectDAO.getProjectById(rs.getInt("id")));
                } catch (Exception e) {
                    System.err.println("Completed projects load failed: " + e.getMessage());
                }
                return result;
            }
        };
        completedTask.setOnSucceeded(ev -> javafx.application.Platform.runLater(() -> {
            completedBox.getChildren().clear();
            List<Project> completedProjects = completedTask.getValue();
            if (completedProjects.isEmpty()) {
                Label empty = new Label("✅ No completed projects yet");
                empty.setStyle("-fx-text-fill: #6c757d;");
                completedBox.getChildren().add(empty);
            } else {
                for (Project p : completedProjects) completedBox.getChildren().add(buildCard(p));
            }
        }));
        new Thread(completedTask).start();
        ScrollPane completedScroll = new ScrollPane(completedBox);
        completedScroll.setFitToWidth(true);
        completed.setContent(completedScroll);

        // Events tab
        Tab eventsTab = new Tab("Events");
        eventsTab.setClosable(false);

        VBox eventsBox = new VBox(10);
        eventsBox.setPadding(new Insets(10));

        int userBatch = SessionManager.getUser().getBatch();
        String userBranch = SessionManager.getUser().getBranch();

        List<model.Event> events = dao.EventDAO.getEventsForStudent(userBatch, userBranch);
        System.out.println("Loading events for batch: " + userBatch + " branch: " + userBranch);
        
        if (events.isEmpty()) {
            Label empty = new Label("📌 No events posted for your batch yet");
            empty.setStyle("-fx-text-fill: #6c757d;");
            eventsBox.getChildren().add(empty);
        } else {
            for (model.Event ev : events) {
                VBox eventCard = new VBox(6);
                eventCard.setPadding(new Insets(14));
                eventCard.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #e94560;" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 0 0 0 3;"
                );
                Label eventTitle = new Label("🎯 " + ev.getTitle());
                eventTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");
                Label eventDesc = new Label(ev.getDescription() != null ? ev.getDescription() : "");
                eventDesc.setWrapText(true);
                eventDesc.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");
                Label eventMeta = new Label(
                    "Batch: " + ev.getTargetBatch() +
                    " | Branch: " + (ev.getTargetBranch() != null ? ev.getTargetBranch() : "All") +
                    " | Skills: " + (ev.getRequiredSkills() != null ? ev.getRequiredSkills() : "Any") +
                    (ev.getEventDate() != null ? " | Date: " + ev.getEventDate() : "")
                );
                eventMeta.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 11px;");
                eventCard.getChildren().addAll(eventTitle, eventDesc, eventMeta);
                eventsBox.getChildren().add(eventCard);
            }
        }

        ScrollPane eventsScroll = new ScrollPane(eventsBox);
        eventsScroll.setFitToWidth(true);
        eventsTab.setContent(eventsScroll);

        tabPane.getTabs().addAll(posted, joined, completed, eventsTab);

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