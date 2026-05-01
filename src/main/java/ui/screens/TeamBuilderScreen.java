package ui.screens;

import java.io.File;
import java.util.List;

import dao.EventDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Team;
import service.CSVExporter;
import service.ManualTeamBuilder;
import service.SkillBalancedTeamBuilder;
import service.SkillMatcher;
import service.TeamBuilder;
import ui.SessionManager;
import java.util.ArrayList;
import dao.UserDAO;
import model.Skill;
import java.util.List;
import model.Student;
import java.util.Map;
import java.util.HashMap; 
import service.CSVExporter;

public class TeamBuilderScreen {

    private List<Team> currentTeams;
    private List<Student> cachedStudents = new ArrayList<>();
    private Map<Integer, Integer> studentScoreCache = new HashMap<>();

    public Scene getScene() {

        HBox root = new HBox();
        root.getChildren().add(
            TeacherHomeScreen.buildSidebar("teambuilder", SessionManager.getUser())
        );

        VBox main = new VBox(0);
        main.setStyle("-fx-background-color: " + TeacherHomeScreen.BG + ";");
        HBox.setHgrow(main, Priority.ALWAYS);

        // ── Top bar ──────────────────────────────────────────────
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(20, 30, 20, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e8eaed;" +
            "-fx-border-width: 0 0 1 0;"
        );

        VBox titleBox = new VBox(2);
        Text pageTitle = new Text("Team Builder");
        pageTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        pageTitle.setFill(Color.web(TeacherHomeScreen.NAVY));
        Label subtitle = new Label("Generate skill-balanced or manual teams for any project");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#8899aa"));
        titleBox.getChildren().addAll(pageTitle, subtitle);
        topBar.getChildren().add(titleBox);

        // ── Two-panel body ────────────────────────────────────────
        HBox body = new HBox(24);
        body.setPadding(new Insets(28, 30, 28, 30));
        VBox.setVgrow(body, Priority.ALWAYS);

        // ── LEFT: form card ───────────────────────────────────────
        VBox formCard = new VBox(18);
        formCard.setPadding(new Insets(24));
        formCard.setPrefWidth(380);
        formCard.setMinWidth(360);
        formCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 2);"
        );

        Text formTitle = new Text("Configuration");
        formTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        formTitle.setFill(Color.web(TeacherHomeScreen.NAVY));

        // Activity Name
        TextField activityNameField = styledField("e.g. DBMS Lab Groups", 310);

        // Required Skills
        TextField requiredSkillsField = styledField("e.g. MySQL, Python, Java", 310);
        // Batch — dropdown from DB
        ComboBox<String> batchBox = new ComboBox<>();
        batchBox.setPromptText("Select Batch");
        batchBox.setPrefWidth(310);
        batchBox.setPrefHeight(38);
        styleCombo(batchBox);

        // Branch — dropdown from DB
        ComboBox<String> branchBox = new ComboBox<>();
        branchBox.setPromptText("Select Branch");
        branchBox.setPrefWidth(310);
        branchBox.setPrefHeight(38);
        styleCombo(branchBox);

        // Team size
        TextField teamSizeField = styledField("e.g. 3", 310);

        // Strategy
        ComboBox<String> strategyBox = new ComboBox<>();
        strategyBox.getItems().addAll("Skill Balanced", "Manual");
        strategyBox.setValue("Skill Balanced");
        strategyBox.setPrefWidth(310);
        strategyBox.setPrefHeight(38);
        styleCombo(strategyBox);

        ProgressIndicator genSpinner = new ProgressIndicator();
        genSpinner.setPrefSize(24, 24);
        genSpinner.setVisible(false);

        Label formStatus = new Label("");
        formStatus.setFont(Font.font("Arial", 12));
        formStatus.setWrapText(true);

        Button generateBtn = new Button("⚡  Generate Teams");
        generateBtn.setPrefHeight(44);
        generateBtn.setPrefWidth(310);
        generateBtn.setStyle(
            "-fx-background-color: " + TeacherHomeScreen.CRIMSON + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        formCard.getChildren().addAll(
            formTitle,
            fieldGroup("Activity Name *", activityNameField),
            fieldGroup("Required Skills", requiredSkillsField),
            fieldGroup("Batch *", batchBox),
            fieldGroup("Branch *", branchBox),
            fieldGroup("Team Size *", teamSizeField),
            fieldGroup("Strategy", strategyBox),
            genSpinner,
            formStatus,
            generateBtn
        );

        // ── RIGHT: results panel ──────────────────────────────────
        VBox resultsPanel = new VBox(16);
        HBox.setHgrow(resultsPanel, Priority.ALWAYS);

        HBox resultsHeader = new HBox(10);
        resultsHeader.setAlignment(Pos.CENTER_LEFT);

        Text resultsTitle = new Text("Generated Teams");
        resultsTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        resultsTitle.setFill(Color.web(TeacherHomeScreen.NAVY));

        Region resSpacer = new Region();
        HBox.setHgrow(resSpacer, Priority.ALWAYS);

        Button exportBtn = new Button("📥  Export CSV");
        exportBtn.setPrefHeight(34);
        exportBtn.setDisable(true);
        exportBtn.setStyle(
            "-fx-background-color: #4f8ef7;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 0 14;" +
            "-fx-cursor: hand;"
        );

        resultsHeader.getChildren().addAll(resultsTitle, resSpacer, exportBtn);

        ScrollPane resultsScroll = new ScrollPane();
        resultsScroll.setFitToWidth(true);
        resultsScroll.setStyle(
            "-fx-background-color: " + TeacherHomeScreen.BG + ";" +
            "-fx-background: " + TeacherHomeScreen.BG + ";"
        );
        VBox.setVgrow(resultsScroll, Priority.ALWAYS);

        VBox resultCards = new VBox(14);
        resultCards.setPadding(new Insets(4));

        Label placeholder = new Label("Configure the form on the left and click Generate Teams.");
        placeholder.setFont(Font.font("Arial", 13));
        placeholder.setTextFill(Color.web("#8899aa"));
        resultCards.getChildren().add(placeholder);
        resultsScroll.setContent(resultCards);

        resultsPanel.getChildren().addAll(resultsHeader, resultsScroll);

        body.getChildren().addAll(formCard, resultsPanel);

        ScrollPane bodyScroll = new ScrollPane(body);
        bodyScroll.setFitToWidth(true);
        bodyScroll.setStyle(
            "-fx-background-color: " + TeacherHomeScreen.BG + ";" +
            "-fx-background: " + TeacherHomeScreen.BG + ";"
        );
        VBox.setVgrow(bodyScroll, Priority.ALWAYS);

        main.getChildren().addAll(topBar, bodyScroll);
        root.getChildren().add(main);
        Scene scene = new Scene(root, 1100, 660);

        // ── Load dropdowns async ──────────────────────────────────
        Task<Object[]> dropTask = new Task<>() {
            @Override protected Object[] call() {
                List<String> batches = EventDAO.getDistinctBatches();
                List<String> branches = EventDAO.getDistinctBranches();
                System.out.println("Batches loaded: " + batches);
                System.out.println("Branches loaded: " + branches);
                return new Object[]{batches, branches};
            }
        };
        dropTask.setOnSucceeded(e -> Platform.runLater(() -> {
            @SuppressWarnings("unchecked") List<String> batches  = (List<String>) dropTask.getValue()[0];
            @SuppressWarnings("unchecked") List<String> branches = (List<String>) dropTask.getValue()[1];
            batchBox.getItems().addAll(batches);
            branchBox.getItems().addAll(branches);
        }));
        dropTask.setOnFailed(e -> System.out.println("Dropdown load FAILED: " + dropTask.getException().getMessage()));
        new Thread(dropTask).start();

        // ── Generate ──────────────────────────────────────────────
        generateBtn.setOnAction(e -> {
            String activityName = activityNameField.getText().trim();
            String skillsInput  = requiredSkillsField.getText().trim();
            String batchStr  = batchBox.getValue();
            String branchStr = branchBox.getValue();
            String sizeStr   = teamSizeField.getText().trim();

            if (activityName.isEmpty() || batchStr == null || branchStr == null || sizeStr.isEmpty()) {
                setStatus(formStatus, "⚠  Please fill all required fields.", false);
                return;
            }
            int batch, teamSize;
            try {
                batch    = Integer.parseInt(batchStr);
                teamSize = Integer.parseInt(sizeStr);
            } catch (Exception ex) {
                setStatus(formStatus, "⚠  Team Size must be a number.", false);
                return;
            }
            List<String> requiredSkills = new ArrayList<>();
            if (!skillsInput.isEmpty()) {
                for (String skill : skillsInput.split(",")) {
                    String trimmed = skill.trim();
                    if (!trimmed.isEmpty()) requiredSkills.add(trimmed);
                }
            }

            generateBtn.setDisable(true);
            genSpinner.setVisible(true);
            formStatus.setText("");
            resultCards.getChildren().clear();
            resultCards.getChildren().add(new Label("Generating…"));
            exportBtn.setDisable(true);
            currentTeams = null;

            String strategy = strategyBox.getValue();
            int finalBatch = batch;

            Task<List<Team>> genTask = new Task<>() {
                @Override protected List<Team> call() {
                    TeamBuilder builder = "Manual".equals(strategy)
                    ? new ManualTeamBuilder(finalBatch, branchStr)
                    : new SkillBalancedTeamBuilder(finalBatch, branchStr, 
                                                    cachedStudents, studentScoreCache);
                                                    return builder.buildTeams(activityName, teamSize, requiredSkills);
                }
                
            };

            genTask.setOnSucceeded(ev -> Platform.runLater(() -> {
                generateBtn.setDisable(false);
                genSpinner.setVisible(false);
                List<Team> teams = genTask.getValue();
                resultCards.getChildren().clear();
            
                if (teams.isEmpty()) {
                    setStatus(formStatus, "⚠  No teams generated.", false);
                    return;
                }
            
                // TWO bulk queries instead of N queries
                Map<Integer, String> nameMap  = SkillMatcher.getAllUserNamesForTeams(teams);
                Map<Integer, String> skillMap = SkillMatcher.getAllSkillStringsForTeams(teams);
            
                currentTeams = teams;
                exportBtn.setDisable(false);
                setStatus(formStatus, "✅  " + teams.size() + " team(s) generated.", true);
            
                for (int i = 0; i < teams.size(); i++) {
                    resultCards.getChildren().add(buildTeamCard(teams.get(i), i + 1, nameMap, skillMap));
                }
            }));

            genTask.setOnFailed(ev -> Platform.runLater(() -> {
                generateBtn.setDisable(false);
                genSpinner.setVisible(false);
                setStatus(formStatus,
                    "❌  Error: " + genTask.getException().getMessage(), false);
            }));

            new Thread(genTask).start();
        });

        // ── Export ────────────────────────────────────────────────
        exportBtn.setOnAction(e -> {
            if (currentTeams == null || currentTeams.isEmpty()) return;

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Team List");
            String activityName = activityNameField.getText().trim();
            chooser.setInitialFileName("teams_" + activityName.replaceAll("\\s+", "_") + ".csv");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            Stage stage = (Stage) root.getScene().getWindow();
            File file = chooser.showSaveDialog(stage);
            if (file == null) return;

            try {
                new CSVExporter().exportToFile(currentTeams, 0, file);
                showInfo("✅  Teams exported to: " + file.getName());
            } catch (Exception ex) {
                showWarn("Export failed: " + ex.getMessage());
            }
        });

        return scene;
    }

    // ── Team result card ──────────────────────────────────────────
        private VBox buildTeamCard(Team team, int teamNum, 
                Map<Integer, String> nameMap,
                Map<Integer, String> skillMap) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(
        "-fx-background-color: white;" +
        "-fx-background-radius: 10;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label numBadge = new Label("Team " + teamNum);
        numBadge.setStyle(
        "-fx-background-color: " + TeacherHomeScreen.NAVY + ";" +
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
        "-fx-text-fill: " + TeacherHomeScreen.CRIMSON + ";" +
        "-fx-font-size: 11px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 3 10;" +
        "-fx-background-radius: 99;"
        );
        header.getChildren().addAll(numBadge, sp, scoreBadge);

        Separator sep = new Separator();

        VBox memberList = new VBox(6);
        for (int memberId : team.getMembers()) {
        String name     = nameMap.getOrDefault(memberId, "Unknown");
        String skillStr = skillMap.getOrDefault(memberId, "No skills listed");

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label dot = new Label("•");
        dot.setTextFill(Color.web(TeacherHomeScreen.CRIMSON));
        dot.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox nameBox = new VBox(2);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(TeacherHomeScreen.NAVY));
        Label skillLbl = new Label(skillStr);
        skillLbl.setFont(Font.font("Arial", 11));
        skillLbl.setTextFill(Color.web("#8899aa"));
        nameBox.getChildren().addAll(nameLbl, skillLbl);

        row.getChildren().addAll(dot, nameBox);
        memberList.getChildren().add(row);
        }

        card.getChildren().addAll(header, sep, memberList);
        return card;
        }

    // ── UI helpers ────────────────────────────────────────────────
    private VBox fieldGroup(String label, javafx.scene.Node field) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#555555"));
        return new VBox(4, lbl, field);
    }

    private TextField styledField(String prompt, double width) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(width);
        f.setPrefHeight(38);
        f.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 10;" +
            "-fx-font-size: 12px;"
        );
        return f;
    }

    private void styleCombo(ComboBox<String> box) {
        box.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 12px;"
        );
    }

    private Label placeholder(String msg) {
        Label l = new Label(msg);
        l.setFont(Font.font("Arial", 13));
        l.setTextFill(Color.web("#8899aa"));
        return l;
    }

    private void setStatus(Label lbl, String msg, boolean success) {
        lbl.setText(msg);
        lbl.setTextFill(success
            ? Color.web("#2ec08a")
            : Color.web(TeacherHomeScreen.CRIMSON));
    }

    private void showWarn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("SkillSync"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("SkillSync"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}