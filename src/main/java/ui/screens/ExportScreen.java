package ui.screens;

import java.io.File;
import java.util.List;

import dao.EventDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Student;
import service.StudentExporter;
import ui.SessionManager;

public class ExportScreen {

    public Scene getScene() {

        HBox root = new HBox();
        root.getChildren().add(
            TeacherHomeScreen.buildSidebar("export", SessionManager.getUser())
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
        Text pageTitle = new Text("Export Data");
        pageTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        pageTitle.setFill(Color.web(TeacherHomeScreen.NAVY));
        Label subtitle = new Label("Download student lists as CSV to your chosen folder");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#8899aa"));
        titleBox.getChildren().addAll(pageTitle, subtitle);
        topBar.getChildren().add(titleBox);

        // ── Body ─────────────────────────────────────────────────
        VBox body = new VBox(24);
        body.setPadding(new Insets(28, 30, 28, 30));

        // ── Export Students Card ──────────────────────────────────
        VBox studentCard = new VBox(16);
        studentCard.setPadding(new Insets(24));
        studentCard.setMaxWidth(520);
        studentCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 2);"
        );

        HBox cardHeader = new HBox(12);
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("👥");
        icon.setStyle(
            "-fx-background-color: #eef3ff;" +
            "-fx-font-size: 20px;" +
            "-fx-padding: 8 10;" +
            "-fx-background-radius: 10;"
        );
        VBox cardInfo = new VBox(2);
        Text cardTitle = new Text("Export Student List");
        cardTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        cardTitle.setFill(Color.web(TeacherHomeScreen.NAVY));
        Label cardDesc = new Label("Download a CSV of all students for a specific batch and branch.");
        cardDesc.setFont(Font.font("Arial", 11));
        cardDesc.setTextFill(Color.web("#8899aa"));
        cardDesc.setWrapText(true);
        cardInfo.getChildren().addAll(cardTitle, cardDesc);
        cardHeader.getChildren().addAll(icon, cardInfo);

        // Batch dropdown
        ComboBox<String> batchBox = new ComboBox<>();
        batchBox.setPromptText("Select Batch *");
        batchBox.setPrefWidth(460);
        batchBox.setPrefHeight(38);
        styleCombo(batchBox);

        // Branch dropdown
        ComboBox<String> branchBox = new ComboBox<>();
        branchBox.setPromptText("Select Branch *");
        branchBox.setPrefWidth(460);
        branchBox.setPrefHeight(38);
        styleCombo(branchBox);

        Label exportStatus = new Label("");
        exportStatus.setFont(Font.font("Arial", 12));
        exportStatus.setWrapText(true);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(22, 22);
        spinner.setVisible(false);

        Button exportStudentsBtn = new Button("📥  Download Student CSV");
        exportStudentsBtn.setPrefHeight(42);
        exportStudentsBtn.setPrefWidth(460);
        exportStudentsBtn.setStyle(
            "-fx-background-color: #4f8ef7;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        studentCard.getChildren().addAll(
            cardHeader,
            fieldGroup("Batch", batchBox),
            fieldGroup("Branch", branchBox),
            spinner,
            exportStatus,
            exportStudentsBtn
        );

        // ── Info card ─────────────────────────────────────────────
        VBox infoCard = new VBox(10);
        infoCard.setPadding(new Insets(18));
        infoCard.setMaxWidth(520);
        infoCard.setStyle(
            "-fx-background-color: #fffbea;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #f7e7a0;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        Text infoTitle = new Text("💡  Tip");
        infoTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        infoTitle.setFill(Color.web("#7a6000"));
        Label infoDesc = new Label(
            "To export teams, use the Team Builder screen — generate your teams first, " +
            "then click 'Export CSV' to save them directly from there."
        );
        infoDesc.setFont(Font.font("Arial", 12));
        infoDesc.setTextFill(Color.web("#7a6000"));
        infoDesc.setWrapText(true);
        infoCard.getChildren().addAll(infoTitle, infoDesc);

        body.getChildren().addAll(studentCard, infoCard);

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background-color: " + TeacherHomeScreen.BG + ";" +
            "-fx-background: " + TeacherHomeScreen.BG + ";"
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        main.getChildren().addAll(topBar, scroll);
        root.getChildren().add(main);

        // ── Load dropdowns async ──────────────────────────────────
        Task<Object[]> dropTask = new Task<>() {
            @Override protected Object[] call() {
                return new Object[]{
                    EventDAO.getDistinctBatches(),
                    EventDAO.getDistinctBranches()
                };
            }
        };
        dropTask.setOnSucceeded(e -> Platform.runLater(() -> {
            @SuppressWarnings("unchecked") List<String> batches  = (List<String>) dropTask.getValue()[0];
            @SuppressWarnings("unchecked") List<String> branches = (List<String>) dropTask.getValue()[1];
            batchBox.getItems().addAll(batches);
            branchBox.getItems().addAll(branches);
        }));
        new Thread(dropTask).start();

        // ── Export handler ────────────────────────────────────────
        exportStudentsBtn.setOnAction(e -> {
            String batchStr  = batchBox.getValue();
            String branchStr = branchBox.getValue();

            if (batchStr == null || branchStr == null) {
                setStatus(exportStatus, "⚠  Please select both batch and branch.", false);
                return;
            }

            int batch;
            try { batch = Integer.parseInt(batchStr); }
            catch (Exception ex) { setStatus(exportStatus, "⚠  Invalid batch value.", false); return; }

            // Open file chooser before going async
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Student List");
            chooser.setInitialFileName("students_" + branchStr + "_" + batchStr + ".csv");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            Stage stage = (Stage) root.getScene().getWindow();
            File file = chooser.showSaveDialog(stage);
            if (file == null) return;

            spinner.setVisible(true);
            exportStudentsBtn.setDisable(true);

            int finalBatch = batch;
            Task<List<Student>> loadTask = new Task<>() {
                @Override protected List<Student> call() {
                    return UserDAO.getStudentsByBatchAndBranch(finalBatch, branchStr);
                }
            };
            loadTask.setOnSucceeded(ev -> Platform.runLater(() -> {
                spinner.setVisible(false);
                exportStudentsBtn.setDisable(false);
                List<Student> students = loadTask.getValue();

                if (students.isEmpty()) {
                    setStatus(exportStatus, "⚠  No students found for selected batch/branch.", false);
                    return;
                }

                try {
                    new StudentExporter().exportToFile(students, file);
                    setStatus(exportStatus,
                        "✅  Exported " + students.size() + " students to: " + file.getName(),
                        true);
                } catch (Exception ex) {
                    setStatus(exportStatus, "❌  Export failed: " + ex.getMessage(), false);
                }
            }));
            loadTask.setOnFailed(ev -> Platform.runLater(() -> {
                spinner.setVisible(false);
                exportStudentsBtn.setDisable(false);
                setStatus(exportStatus,
                    "❌  Failed: " + loadTask.getException().getMessage(), false);
            }));
            new Thread(loadTask).start();
        });

        return new Scene(root, 1100, 660);
    }

    private VBox fieldGroup(String label, javafx.scene.Node field) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#555555"));
        return new VBox(4, lbl, field);
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

    private void setStatus(Label lbl, String msg, boolean success) {
        lbl.setText(msg);
        lbl.setTextFill(success ? Color.web("#2ec08a") : Color.web(TeacherHomeScreen.CRIMSON));
    }
}