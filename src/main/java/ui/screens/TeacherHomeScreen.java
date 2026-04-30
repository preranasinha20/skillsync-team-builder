package ui.screens;

import dao.EventDAO;
import dao.ProjectDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import ui.LoginScreen;
import ui.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherHomeScreen {

    // ── Shared design tokens (used by all teacher screens) ────────
    public static final String NAVY    = "#1a1a2e";
    public static final String CRIMSON = "#e94560";
    public static final String BG      = "#f0f2f5";
    public static final String SIDEBAR = "#16213e";

    // ─────────────────────────────────────────────────────────────
    //  getScene
    // ─────────────────────────────────────────────────────────────
    public Scene getScene() {

        User teacher = SessionManager.getUser();

        HBox root = new HBox();
        root.getChildren().add(buildSidebar("overview", teacher));

        // ── Content area ─────────────────────────────────────────
        VBox main = new VBox(0);
        main.setStyle("-fx-background-color: " + BG + ";");
        HBox.setHgrow(main, Priority.ALWAYS);

        // Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(20, 30, 20, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: " + NAVY + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 8, 0, 0, 2);"
        );

        VBox titleBox = new VBox(3);
        String firstName = teacher != null
            ? teacher.getName().split(" ")[0] : "Teacher";

        Text welcome = new Text("Welcome back, " + firstName + " 👋");
        welcome.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        welcome.setFill(Color.WHITE);

        String today = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy"));
        Label dateLabel = new Label(today);
        dateLabel.setFont(Font.font("Arial", 12));
        dateLabel.setTextFill(Color.web("#8899aa"));
        titleBox.getChildren().addAll(welcome, dateLabel);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        String dept = teacher != null && teacher.getBranch() != null
            ? teacher.getBranch() : "Faculty";
        Label deptChip = new Label("🏫  " + dept);
        deptChip.setStyle(
            "-fx-background-color: rgba(255,255,255,0.12);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 5 14;" +
            "-fx-background-radius: 99;"
        );

        topBar.getChildren().addAll(titleBox, topSpacer, deptChip);

        // Scrollable body
        VBox body = new VBox(28);
        body.setPadding(new Insets(28, 30, 28, 30));

        // ── Stats row (loaded async to avoid freeze) ──────────────
        Label statsLoading = new Label("Loading stats…");
        statsLoading.setFont(Font.font("Arial", 12));
        statsLoading.setTextFill(Color.web("#8899aa"));

        HBox statsRow = new HBox(16);
        statsRow.getChildren().add(statsLoading);

        // ── Quick actions ─────────────────────────────────────────
        Text actionsTitle = new Text("Quick Actions");
        actionsTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        actionsTitle.setFill(Color.web(NAVY));

        HBox actionsRow = new HBox(16);
        actionsRow.getChildren().addAll(
            quickCard("👥", "View Students",
                "Browse students by batch & branch", "#4f8ef7",
                () -> switchScene(actionsRow, new ViewStudentsScreen().getScene())),
            quickCard("⚡", "Build Teams",
                "Auto-generate skill-balanced teams", CRIMSON,
                () -> switchScene(actionsRow, new TeamBuilderScreen().getScene())),
            quickCard("📅", "Post Event",
                "Create events for specific batches", "#2ec08a",
                () -> switchScene(actionsRow, new PostEventScreen().getScene())),
            quickCard("📤", "Export Data",
                "Download student lists as CSV", "#f7a94f",
                () -> switchScene(actionsRow, new ExportScreen().getScene()))
        );

        // ── How it works ──────────────────────────────────────────
        Text howTitle = new Text("How it works");
        howTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        howTitle.setFill(Color.web(NAVY));

        HBox howRow = new HBox(16);
        howRow.getChildren().addAll(
            howCard("1", "Register Students",
                "Students register with their branch, batch, and skills."),
            howCard("2", "Post Events",
                "Create events targeted at specific batches or branches."),
            howCard("3", "Build Teams",
                "Enter a project ID and let SkillSync match the best team."),
            howCard("4", "Export",
                "Download student lists or generated teams as CSV files.")
        );

        body.getChildren().addAll(
            statsRow, actionsTitle, actionsRow, howTitle, howRow
        );

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + BG + "; -fx-background: " + BG + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        main.getChildren().addAll(topBar, scroll);
        root.getChildren().add(main);

        Scene scene = new Scene(root, 1100, 660);

        // ── Load stats asynchronously (no freeze on startup) ──────
        Task<int[]> statsTask = new Task<>() {
            @Override
            protected int[] call() {
                int open     = 0;
                int ongoing  = 0;
                int batches  = 0;
                try { open    = ProjectDAO.getAllOpenProjects().size(); }    catch (Exception ignored) {}
                try { ongoing = ProjectDAO.getProjectsByStatus("ONGOING").size(); } catch (Exception ignored) {}
                try { batches = EventDAO.getDistinctBatches().size(); }     catch (Exception ignored) {}
                return new int[]{open, ongoing, batches};
            }
        };
        statsTask.setOnSucceeded(e -> Platform.runLater(() -> {
            int[] vals = statsTask.getValue();
            statsRow.getChildren().setAll(
                statCard("Open Projects",    String.valueOf(vals[0]), "📁", "#4f8ef7", "#eef3ff"),
                statCard("Ongoing Projects", String.valueOf(vals[1]), "🔄", "#2ec08a", "#edfbf5"),
                statCard("Student Batches",  String.valueOf(vals[2]), "🎓", CRIMSON,   "#fff0f3"),
                statCard("Team Builder",     "Active",                "⚡", "#f7a94f", "#fff8ee")
            );
        }));
        statsTask.setOnFailed(e -> Platform.runLater(() ->
            statsRow.getChildren().setAll(new Label("Could not load stats."))));
        new Thread(statsTask).start();

        return scene;
    }

    // ─────────────────────────────────────────────────────────────
    //  Static sidebar builder — shared by ALL teacher screens
    // ─────────────────────────────────────────────────────────────
    public static VBox buildSidebar(String activeKey, User teacher) {

        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(230);
        sidebar.setMinWidth(230);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR + ";");

        // Logo
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(26, 20, 22, 20));
        logoBox.setStyle("-fx-background-color: " + NAVY + ";");
        Text logo = new Text("SkillSync");
        logo.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        logo.setFill(Color.WHITE);
        Text sub = new Text("Teacher Portal");
        sub.setFont(Font.font("Georgia", 11));
        sub.setFill(Color.web(CRIMSON));
        logoBox.getChildren().addAll(logo, sub);

        // Avatar row
        VBox avatarBox = new VBox(0);
        avatarBox.setPadding(new Insets(14, 20, 14, 20));
        avatarBox.setStyle("-fx-background-color: rgba(255,255,255,0.04);");

        String name = teacher != null ? teacher.getName() : "Teacher";
        String[] parts = name.trim().split("\\s+");
        String initials = parts.length >= 2
            ? ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase()
            : name.substring(0, Math.min(2, name.length())).toUpperCase();

        StackPane av = new StackPane();
        Rectangle avBg = new Rectangle(38, 38);
        avBg.setArcWidth(38); avBg.setArcHeight(38);
        avBg.setFill(Color.web(CRIMSON));
        Text avText = new Text(initials);
        avText.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        avText.setFill(Color.WHITE);
        av.getChildren().addAll(avBg, avText);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 12));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setWrapText(true);
        String dept = teacher != null && teacher.getBranch() != null
            ? teacher.getBranch() : "Faculty";
        Label deptLabel = new Label(dept);
        deptLabel.setFont(Font.font("Arial", 10));
        deptLabel.setTextFill(Color.web("#8899aa"));

        HBox avRow = new HBox(10, av, new VBox(2, nameLabel, deptLabel));
        avRow.setAlignment(Pos.CENTER_LEFT);
        avatarBox.getChildren().add(avRow);

        // Nav items
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(14, 8, 14, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);

        nav.getChildren().addAll(
            navItem("🏠", "Overview",      "overview",    activeKey),
            navItem("👥", "View Students", "students",    activeKey),
            navItem("⚡", "Team Builder",  "teambuilder", activeKey),
            navItem("📅", "Post Event",    "postevent",   activeKey),
            navItem("📤", "Export Data",   "export",      activeKey)
        );

        // Logout
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.08);");
        Button logout = new Button("⬅  Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setPrefHeight(40);
        logout.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #8899aa;" +
            "-fx-font-size: 13px;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 0 0 0 14;" +
            "-fx-cursor: hand;"
        );
        logout.setOnMouseEntered(e -> logout.setStyle(
            "-fx-background-color: rgba(233,69,96,0.15);" +
            "-fx-text-fill: " + CRIMSON + ";" +
            "-fx-font-size: 13px;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 0 0 0 14;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;"
        ));
        logout.setOnMouseExited(e -> logout.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #8899aa;" +
            "-fx-font-size: 13px;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 0 0 0 14;" +
            "-fx-cursor: hand;"
        ));
        logout.setOnAction(e -> {
            SessionManager.clear();
            Stage stage = (Stage) sidebar.getScene().getWindow();
            stage.setScene(new LoginScreen(stage).getLoginScene());
        });

        VBox bottom = new VBox(sep, logout);
        bottom.setPadding(new Insets(0, 8, 16, 8));

        sidebar.getChildren().addAll(logoBox, avatarBox, nav, bottom);

        // Wire nav clicks
        Button ovBtn  = (Button) nav.getChildren().get(0);
        Button stBtn  = (Button) nav.getChildren().get(1);
        Button tbBtn  = (Button) nav.getChildren().get(2);
        Button peBtn  = (Button) nav.getChildren().get(3);
        Button exBtn  = (Button) nav.getChildren().get(4);

        ovBtn.setOnAction(e -> switchSceneFromNode(sidebar, new TeacherHomeScreen().getScene()));
        stBtn.setOnAction(e -> switchSceneFromNode(sidebar, new ViewStudentsScreen().getScene()));
        tbBtn.setOnAction(e -> switchSceneFromNode(sidebar, new TeamBuilderScreen().getScene()));
        peBtn.setOnAction(e -> switchSceneFromNode(sidebar, new PostEventScreen().getScene()));
        exBtn.setOnAction(e -> switchSceneFromNode(sidebar, new ExportScreen().getScene()));

        return sidebar;
    }

    // ── Helpers ───────────────────────────────────────────────────
    private static Button navItem(String icon, String label, String key, String activeKey) {
        boolean active = key.equals(activeKey);
        Button btn = new Button(icon + "   " + label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(44);
        btn.setStyle(
            (active
                ? "-fx-background-color: rgba(233,69,96,0.18); -fx-text-fill: " + CRIMSON + ";"
                : "-fx-background-color: transparent; -fx-text-fill: #8899aa;") +
            "-fx-font-size: 13px;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 0 0 0 14;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;"
        );
        if (!active) {
            btn.setOnMouseEntered(ev -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 0 0 0 14;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 8;"
            ));
            btn.setOnMouseExited(ev -> btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #8899aa;" +
                "-fx-font-size: 13px;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 0 0 0 14;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 8;"
            ));
        }
        return btn;
    }

    /** Convenience — returns the currently logged-in teacher from session. */
    public static User getCurrentTeacher() {
        return ui.SessionManager.getUser();
    }

    public static void switchSceneFromNode(javafx.scene.Node node, Scene scene) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(scene);
    }

    private void switchScene(javafx.scene.Node node, Scene scene) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(scene);
    }

    private VBox statCard(String label, String value, String icon,
                          String accentColor, String bgColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 2);"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-font-size: 18px;" +
            "-fx-padding: 8 10;" +
            "-fx-background-radius: 8;"
        );
        Text val = new Text(value);
        val.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        val.setFill(Color.web(accentColor));
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setTextFill(Color.web("#8899aa"));
        card.getChildren().addAll(iconLbl, val, lbl);
        return card;
    }

    private VBox quickCard(String icon, String title, String desc,
                           String color, Runnable onClick) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 2);"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(22));
        Text titleT = new Text(title);
        titleT.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        titleT.setFill(Color.web(NAVY));
        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 11));
        descLbl.setTextFill(Color.web("#8899aa"));
        descLbl.setWrapText(true);
        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");

        card.getChildren().addAll(iconLbl, titleT, descLbl, accent);
        card.setOnMouseClicked(e -> onClick.run());
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 18, 0, 0, 4);" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1.5;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 2);"
        ));
        return card;
    }

    private VBox howCard(String num, String title, String desc) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e8eaed;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        Label numLbl = new Label(num);
        numLbl.setStyle(
            "-fx-background-color: " + CRIMSON + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 3 9;" +
            "-fx-background-radius: 99;"
        );
        Text titleT = new Text(title);
        titleT.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        titleT.setFill(Color.web(NAVY));
        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 11));
        descLbl.setTextFill(Color.web("#6c757d"));
        descLbl.setWrapText(true);
        card.getChildren().addAll(numLbl, titleT, descLbl);
        return card;
    }
}