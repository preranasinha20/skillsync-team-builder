package ui.screens;

import dao.ProjectDAO;
import dao.TeamRequestDAO;
import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import model.Team;
import model.Project;
import model.Skill;
import service.SkillMatcher;
import ui.HomeFeedScreen;
import ui.SessionManager;

import java.util.List;

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

        // ── Navbar ───────────────────────────────────────────────
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
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #dcdcdc; -fx-cursor: hand;");
        back.setOnAction(e -> new HomeFeedScreen(stage).show());
        navbar.getChildren().addAll(title, spacer, back);

        // ── Project info card ────────────────────────────────────
        VBox infoCard = new VBox(12);
        infoCard.setPadding(new Insets(24));
        infoCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 2);"
        );

        Text projName = new Text(project.getTitle());
        projName.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        projName.setFill(Color.web("#1a1a2e"));

        Text desc = new Text(project.getDescription());
        desc.setFill(Color.web("#555555"));
        desc.setWrappingWidth(500);

        HBox tags = new HBox(10);
        tags.getChildren().addAll(
            chip(project.getBranch()),
            chip("Batch " + project.getBatch()),
            chip("Team size: " + project.getTeamSize())
        );

        // Required skills
        List<String> reqSkills = ProjectDAO.getProjectSkills(project.getId());
        Label skillsLabel = new Label("Required: " + String.join(", ", reqSkills));
        skillsLabel.setFont(Font.font("Arial", 12));
        skillsLabel.setTextFill(Color.web("#6c757d"));

        infoCard.getChildren().addAll(projName, desc, tags, skillsLabel);

        // ── Suggested teams section ──────────────────────────────
        Text suggestTitle = new Text("Suggested Teams");
        suggestTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        suggestTitle.setFill(Color.web("#1a1a2e"));

        Label loadingLabel = new Label("⏳ Finding best matches...");
        loadingLabel.setFont(Font.font("Arial", 13));
        loadingLabel.setTextFill(Color.web("#6c757d"));

        VBox teamsBox = new VBox(16);
        teamsBox.getChildren().add(loadingLabel);

        // Load suggestions in background
        javafx.concurrent.Task<List<Team>> matchTask = new javafx.concurrent.Task<>() {
            @Override protected List<Team> call() {
                return SkillMatcher.getTopTeams(
                    project.getId(),
                    project.getBatch(),
                    project.getBranch(),
                    project.getTeamSize() - 1,  // subtract 1 for the owner
                    project.getOwnerId()         // exclude owner from suggestions
                );
            }
        };

        matchTask.setOnSucceeded(ev -> javafx.application.Platform.runLater(() -> {
            teamsBox.getChildren().clear();
            List<Team> teams = matchTask.getValue();

            if (teams.isEmpty()) {
                teamsBox.getChildren().add(
                    new Label("No suggestions found. Not enough students in this batch/branch.")
                );
                return;
            }

            for (int t = 0; t < teams.size(); t++) {
                Team team = teams.get(t);
                teamsBox.getChildren().add(buildTeamSuggestionCard(team, t + 1));
            }
        }));

        matchTask.setOnFailed(ev -> javafx.application.Platform.runLater(() -> {
            teamsBox.getChildren().clear();
            teamsBox.getChildren().add(new Label("Could not load suggestions."));
        }));

        new Thread(matchTask).start();

        // ── Scroll layout ────────────────────────────────────────
        VBox body = new VBox(20);
        body.setPadding(new Insets(24));
        body.getChildren().addAll(infoCard, suggestTitle, teamsBox);

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f4f6f9; -fx-background: #f4f6f9;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(navbar, scroll);
        stage.setScene(new Scene(root, 920, 620));
        stage.show();
    }

    // ── Team suggestion card ─────────────────────────────────────
    private VBox buildTeamSuggestionCard(Team team, int teamNum) {

        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 2);"
        );

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label teamBadge = new Label("Suggested Team " + teamNum);
        teamBadge.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 3 10;" +
            "-fx-background-radius: 99;"
        );

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label scoreBadge = new Label(String.format("%.0f%% match", team.getMatchScore()));
        scoreBadge.setStyle(
            "-fx-background-color: #fff0f3;" +
            "-fx-text-fill: #e94560;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 3 10;" +
            "-fx-background-radius: 99;"
        );

        header.getChildren().addAll(teamBadge, sp, scoreBadge);

        // Members with skills
        VBox memberList = new VBox(8);
        for (int memberId : team.getMembers()) {
            model.User member = UserDAO.getUserById(memberId);
            if (member == null) continue;

            List<Skill> skills = UserDAO.getSkillsByUser(memberId);
            String skillStr = skills.stream()
                .map(s -> s.getSkillName() + "(" + s.getProficiency().charAt(0) + ")")
                .collect(java.util.stream.Collectors.joining(", "));

            HBox memberRow = new HBox(8);
            memberRow.setAlignment(Pos.CENTER_LEFT);
            memberRow.setPadding(new Insets(8, 12, 8, 12));
            memberRow.setStyle(
                "-fx-background-color: #f8f9fa;" +
                "-fx-background-radius: 6;"
            );

            VBox memberInfo = new VBox(2);
            Label nameLbl = new Label(member.getName());
            nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            nameLbl.setTextFill(Color.web("#1a1a2e"));
            Label skillLbl = new Label(skillStr.isEmpty() ? "No skills listed" : skillStr);
            skillLbl.setFont(Font.font("Arial", 11));
            skillLbl.setTextFill(Color.web("#6c757d"));
            memberInfo.getChildren().addAll(nameLbl, skillLbl);

            Region memberSpacer = new Region();
            HBox.setHgrow(memberSpacer, Priority.ALWAYS);

            memberRow.getChildren().addAll(memberInfo, memberSpacer);
            memberList.getChildren().add(memberRow);
        }

        // Invite button — sends request to ALL members of this team
        Button inviteBtn = new Button("✉ Invite This Team");
        inviteBtn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 20;" +
            "-fx-cursor: hand;"
        );

        Label feedback = new Label("");
        feedback.setFont(Font.font("Arial", 12));

        inviteBtn.setOnAction(e -> {
            int senderId = SessionManager.getUser().getId();
            int successCount = 0;

            for (int receiverId : team.getMembers()) {
                // Don't send invite to yourself
                if (receiverId == senderId) continue;

                boolean ok = TeamRequestDAO.sendRequest(
                    project.getId(),
                    senderId,
                    receiverId,
                    "INVITE",
                    "You've been suggested for this project team!"
                );
                if (ok) successCount++;
            }

            if (successCount > 0) {
                feedback.setText("✅ Invites sent to " + successCount + " members!");
                feedback.setTextFill(Color.web("#2ecc71"));
                inviteBtn.setDisable(true);
            } else {
                feedback.setText("⚠ Already invited or failed.");
                feedback.setTextFill(Color.web("#e94560"));
            }
        });

        card.getChildren().addAll(header, memberList, inviteBtn, feedback);
        return card;
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