package ui;

import java.util.List;
import dao.TeamRequestDAO;
import dao.ProjectDAO;
import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Project;
import model.User;
import ui.screens.PostProjectScreen;
import ui.screens.ProjectDetailScreen;

public class HomeFeedScreen {

    private Stage stage;
    private User currentUser;

    public HomeFeedScreen(Stage stage) {
        this.stage = stage;
        this.currentUser = SessionManager.getUser();
    }

    public void show() {

// ── Top navbar ───────────────────────────────────────────
HBox navbar = new HBox();
navbar.setPadding(new Insets(0, 24, 0, 24));
navbar.setPrefHeight(56);
navbar.setAlignment(Pos.CENTER_LEFT);
navbar.setStyle("-fx-background-color: #1a1a2e;");

Text logo = new Text("SkillSync");
logo.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
logo.setFill(Color.WHITE);

Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS);

// ✅ username FIRST
String userName = currentUser != null ? currentUser.getName() : "Guest";

Label userLabel = new Label("👤  " + userName);
userLabel.setFont(Font.font("Arial", 13));
userLabel.setTextFill(Color.web("#aaaaaa"));

// ✅ request count
int requestCount = TeamRequestDAO.getPendingRequestCount(
        SessionManager.getUser().getId()
);

// ✅ buttons
Button inboxBtn = navButton(
        "Inbox" + (requestCount > 0 ? " (" + requestCount + ")" : "")
);

Button profileBtn = navButton("My Profile");
Button logoutBtn  = navButton("Logout");

// ✅ actions (ONLY ONCE)
inboxBtn.setOnAction(e -> {
    stage.setScene(new ui.screens.InboxScreen().getScene());
});

profileBtn.setOnAction(e -> new ProfileScreen(stage).show());

logoutBtn.setOnAction(e -> {
    SessionManager.clear();
    new LoginScreen(stage).show();
});

// ✅ add to navbar
navbar.getChildren().addAll(
        logo, spacer, userLabel,
        new Label("   "), inboxBtn,
        new Label("   "), profileBtn,
        new Label("   "), logoutBtn
);
        // ── Filter bar ───────────────────────────────────────────
        HBox filterBar = new HBox(12);
        filterBar.setPadding(new Insets(16, 24, 16, 24));
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

        Text feedTitle = new Text("Open Projects");
        feedTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        feedTitle.setFill(Color.web("#1a1a2e"));

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        TextField branchFilter = new TextField();
        branchFilter.setPromptText("Filter by branch");
        branchFilter.setPrefWidth(160);
        styleField(branchFilter);

        TextField batchFilter = new TextField();
        batchFilter.setPromptText("Filter by batch");
        batchFilter.setPrefWidth(140);
        styleField(batchFilter);

        Button filterBtn = new Button("Search");
        styleAccentButton(filterBtn);

        Button clearBtn = new Button("Clear");
        styleOutlineButton(clearBtn);

        // ── Post project button ───────────────────────────────────
        Button postBtn = new Button("+ Post Project");
        stylePrimaryButton(postBtn);
        postBtn.setOnAction(e -> {
        stage.setScene(new PostProjectScreen(stage).getScene());
        });

        filterBar.getChildren().addAll(
            feedTitle, filterSpacer,
            branchFilter, batchFilter,
            filterBtn, clearBtn, postBtn
        );

        // ── Project cards area ───────────────────────────────────
        VBox cardsContainer = new VBox(16);
        cardsContainer.setPadding(new Insets(24));

        // Load and display projects
        loadProjects(cardsContainer, null, null);

        filterBtn.setOnAction(e -> {
            String branch = branchFilter.getText().trim();
            String batchStr = batchFilter.getText().trim();
            loadProjects(cardsContainer, 
                branch.isEmpty() ? null : branch,
                batchStr.isEmpty() ? null : batchStr);
        });

        clearBtn.setOnAction(e -> {
            branchFilter.clear();
            batchFilter.clear();
            loadProjects(cardsContainer, null, null);
        });

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f0f2f5; -fx-background: #f0f2f5;");

        // ── Root ─────────────────────────────────────────────────
        VBox root = new VBox(navbar, filterBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setStyle("-fx-background-color: #f0f2f5;");

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("SkillSync — Home");
        stage.setScene(scene);
        stage.show();
    }

    // ── Load projects into cards ─────────────────────────────────
    private void loadProjects(VBox container, String branch, String batchStr) {
        container.getChildren().clear();

        List<Project> projects;
        if (branch != null || batchStr != null) {
            int batch = 0;
            try { batch = Integer.parseInt(batchStr); } catch (Exception e) {}
            projects = ProjectDAO.getOpenProjectsByBatchAndBranch(batch, branch);
        } else {
            projects = ProjectDAO.getAllOpenProjects();
        }

        if (projects.isEmpty()) {
            Label empty = new Label("No open projects found. Be the first to post one!");
            empty.setFont(Font.font("Arial", 14));
            empty.setTextFill(Color.web("#6c757d"));
            empty.setPadding(new Insets(40));
            container.getChildren().add(empty);
            return;
        }

        for (Project project : projects) {
            container.getChildren().add(buildProjectCard(project));
        }
    }

    // ── Project card ─────────────────────────────────────────────
    private VBox buildProjectCard(Project project) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        // Title row
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Text projectTitle = new Text(project.getTitle());
        projectTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        projectTitle.setFill(Color.web("#1a1a2e"));

        Label statusBadge = new Label(project.getStatusString());
        statusBadge.setStyle(
            "-fx-background-color: #e8f5e9;" +
            "-fx-text-fill: #2e7d32;" +
            "-fx-padding: 2 10;" +
            "-fx-background-radius: 99;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);

        Label teamSizeLabel = new Label("👥 Team size: " + project.getTeamSize());
        teamSizeLabel.setFont(Font.font("Arial", 12));
        teamSizeLabel.setTextFill(Color.web("#6c757d"));

        titleRow.getChildren().addAll(projectTitle, statusBadge, titleSpacer, teamSizeLabel);

        // Description
        Label desc = new Label(project.getDescription());
        desc.setFont(Font.font("Arial", 13));
        desc.setTextFill(Color.web("#555555"));
        desc.setWrapText(true);

        // Meta row — branch, batch, deadline
        HBox metaRow = new HBox(16);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        Label branchLabel = metaTag(project.getBranch());
        Label batchLabel  = metaTag(String.valueOf(project.getBatch()));

        // Posted by
        User owner = UserDAO.getUserById(project.getOwnerId());
        String ownerName = owner != null ? owner.getName() : "Unknown";
        Label ownerLabel = metaTag("👤 " + ownerName);

        metaRow.getChildren().addAll(branchLabel, batchLabel, ownerLabel);

        // View button
        Button viewBtn = new Button("View & Find Team →");
        viewBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #e94560;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #e94560;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 6 16;"
        );
        viewBtn.setOnMouseEntered(e -> viewBtn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #e94560;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 6 16;"
        ));
        viewBtn.setOnMouseExited(e -> viewBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #e94560;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #e94560;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 6 16;"
        ));

        viewBtn.setOnAction(e -> {
    new ProjectDetailScreen(stage, project).show();
});

        card.getChildren().addAll(titleRow, desc, metaRow, viewBtn);
        return card;
    }

    // ── Helpers ──────────────────────────────────────────────────
    private Label metaTag(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setTextFill(Color.web("#6c757d"));
        lbl.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-padding: 3 10;" +
            "-fx-background-radius: 4;"
        );
        return lbl;
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #aaaaaa;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #aaaaaa;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    private void styleField(TextField field) {
        field.setPrefHeight(36);
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 10;" +
            "-fx-font-size: 12px;"
        );
    }

    private void stylePrimaryButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
    }

    private void styleAccentButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
    }

    private void styleOutlineButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #6c757d;" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SkillSync");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}