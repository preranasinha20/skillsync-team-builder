package ui.screens;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import dao.EventDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Skill;
import model.Student;
import service.StudentExporter;

public class ViewStudentsScreen {

    private final ObservableList<Student> tableData = FXCollections.observableArrayList();

    public Scene getScene() {

        HBox root = new HBox();
        root.getChildren().add(
            TeacherHomeScreen.buildSidebar("students", TeacherHomeScreen.getCurrentTeacher())
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
        Text pageTitle = new Text("View Students");
        pageTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        pageTitle.setFill(Color.web(TeacherHomeScreen.NAVY));
        Label subtitle = new Label("Browse and filter registered students by batch and branch");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#8899aa"));
        titleBox.getChildren().addAll(pageTitle, subtitle);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Label countBadge = new Label("0 students");
        countBadge.setStyle(
            "-fx-background-color: #eef3ff;" +
            "-fx-text-fill: #4f8ef7;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 14;" +
            "-fx-background-radius: 99;"
        );

        Button exportBtn = actionButton("📥  Export CSV", "#2ec08a");
        topBar.getChildren().addAll(titleBox, topSpacer, countBadge,
            new Label("  "), exportBtn);

        // ── Filter bar ───────────────────────────────────────────
        HBox filterBar = new HBox(14);
        filterBar.setPadding(new Insets(14, 30, 14, 30));
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e8eaed;" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Dropdowns populated from DB asynchronously
        ComboBox<String> batchBox = new ComboBox<>();
        batchBox.setPromptText("Select Batch");
        batchBox.setPrefWidth(160);
        batchBox.setPrefHeight(36);
        styleCombo(batchBox);

        ComboBox<String> branchBox = new ComboBox<>();
        branchBox.setPromptText("Select Branch");
        branchBox.setPrefWidth(170);
        branchBox.setPrefHeight(36);
        styleCombo(branchBox);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Search by name…");
        searchField.setPrefWidth(210);
        searchField.setPrefHeight(36);
        searchField.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 10;" +
            "-fx-font-size: 12px;"
        );

        Button loadBtn   = actionButton("Load", TeacherHomeScreen.CRIMSON);
        Button clearBtn  = outlineButton("Clear");

        ProgressIndicator loadSpinner = new ProgressIndicator();
        loadSpinner.setPrefSize(22, 22);
        loadSpinner.setVisible(false);

        filterBar.getChildren().addAll(
            fieldGroup("Batch",  batchBox),
            fieldGroup("Branch", branchBox),
            fieldGroup("Search", searchField),
            loadBtn, clearBtn, loadSpinner
        );

        // ── Table ────────────────────────────────────────────────
        TableView<Student> table = new TableView<>(tableData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(52);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent;" +
            "-fx-table-cell-border-color: #f0f2f5;"
        );
        table.setPlaceholder(
            placeholder("Select batch and branch above, then click Load")
        );

        // Name
        TableColumn<Student, String> nameCol = new TableColumn<>("NAME");
        nameCol.setPrefWidth(210);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) { setGraphic(null); return; }
                HBox cell = new HBox(10);
                cell.setAlignment(Pos.CENTER_LEFT);
                StackPane av = new StackPane();
                Rectangle bg = new Rectangle(32, 32);
                bg.setArcWidth(32); bg.setArcHeight(32);
                bg.setFill(Color.web("#eef3ff"));
                Text init = new Text(name.substring(0, 1).toUpperCase());
                init.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
                init.setFill(Color.web("#4f8ef7"));
                av.getChildren().addAll(bg, init);
                Label lbl = new Label(name);
                lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                lbl.setTextFill(Color.web(TeacherHomeScreen.NAVY));
                cell.getChildren().addAll(av, lbl);
                setGraphic(cell); setText(null);
            }
        });

        // Email
        TableColumn<Student, String> emailCol = new TableColumn<>("EMAIL");
        emailCol.setPrefWidth(210);
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        emailCol.setCellFactory(col -> plainCell("#555555", 12));

        // Batch
        TableColumn<Student, String> batchCol = new TableColumn<>("BATCH");
        batchCol.setPrefWidth(80);
        batchCol.setCellValueFactory(d ->
            new SimpleStringProperty(String.valueOf(d.getValue().getBatch())));
        batchCol.setCellFactory(col -> chipCell("#eef3ff", "#4f8ef7"));

        // Branch
        TableColumn<Student, String> branchCol = new TableColumn<>("BRANCH");
        branchCol.setPrefWidth(100);
        branchCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBranch()));
        branchCol.setCellFactory(col -> chipCell("#fff0f3", TeacherHomeScreen.CRIMSON));

        // Skills
        TableColumn<Student, String> skillCol = new TableColumn<>("SKILLS");
        skillCol.setPrefWidth(320);
        skillCol.setCellValueFactory(d -> {
            // NOTE: this runs on FX thread; skills loaded per-row
            List<Skill> skills = UserDAO.getSkillsByUser(d.getValue().getId());
            return new SimpleStringProperty(
                skills.stream().map(Skill::getSkillName).collect(Collectors.joining(", "))
            );
        });
        skillCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String sk, boolean empty) {
                super.updateItem(sk, empty);
                if (empty || sk == null || sk.isBlank()) { setGraphic(null); setText(null); return; }
                HBox chips = new HBox(5);
                chips.setAlignment(Pos.CENTER_LEFT);
                for (String s : sk.split(", ")) {
                    if (s.isBlank()) continue;
                    Label chip = new Label(s.trim());
                    chip.setStyle(
                        "-fx-background-color: #edfbf5;" +
                        "-fx-text-fill: #2ec08a;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 2 8;" +
                        "-fx-background-radius: 99;"
                    );
                    chips.getChildren().add(chip);
                }
                setGraphic(chips); setText(null);
            }
        });

        table.getColumns().addAll(nameCol, emailCol, batchCol, branchCol, skillCol);

        // Style header via CSS string
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: transparent;"
        );

        VBox tableWrapper = new VBox(table);
        tableWrapper.setPadding(new Insets(20, 30, 20, 30));
        tableWrapper.setStyle("-fx-background-color: " + TeacherHomeScreen.BG + ";");
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(tableWrapper, Priority.ALWAYS);

        main.getChildren().addAll(topBar, filterBar, tableWrapper);
        root.getChildren().add(main);
        Scene scene = new Scene(root, 1100, 660);

        // ── Load dropdowns asynchronously (no freeze) ─────────────
        Task<Object[]> dropdownTask = new Task<>() {
            @Override protected Object[] call() {
                return new Object[]{
                    EventDAO.getDistinctBatches(),
                    EventDAO.getDistinctBranches()
                };
            }
        };
        dropdownTask.setOnSucceeded(e -> Platform.runLater(() -> {
            @SuppressWarnings("unchecked") List<String> batches  = (List<String>) dropdownTask.getValue()[0];
            @SuppressWarnings("unchecked") List<String> branches = (List<String>) dropdownTask.getValue()[1];
            batchBox.getItems().addAll(batches);
            branchBox.getItems().addAll(branches);
        }));
        dropdownTask.setOnFailed(e ->
            System.err.println("[ViewStudents] dropdown load failed: " +
                dropdownTask.getException().getMessage()));
        new Thread(dropdownTask).start();

        // ── Load students ─────────────────────────────────────────
        loadBtn.setOnAction(e -> {
            String batchStr  = batchBox.getValue();
            String branchStr = branchBox.getValue();

            if (batchStr == null || branchStr == null) {
                showWarn(root, "Please select both a batch and a branch.");
                return;
            }
            int batch;
            try { batch = Integer.parseInt(batchStr); }
            catch (Exception ex) { showWarn(root, "Invalid batch value."); return; }

            loadSpinner.setVisible(true);
            loadBtn.setDisable(true);
            tableData.clear();
            countBadge.setText("Loading…");

            int finalBatch = batch;
            Task<List<Student>> loadTask = new Task<>() {
                @Override protected List<Student> call() {
                    return UserDAO.getStudentsByBatchAndBranch(finalBatch, branchStr);
                }
            };
            loadTask.setOnSucceeded(ev -> Platform.runLater(() -> {
                loadSpinner.setVisible(false);
                loadBtn.setDisable(false);
                List<Student> students = loadTask.getValue();
                tableData.setAll(students);
                table.setItems(tableData);
                String q = searchField.getText().trim().toLowerCase();
                if (!q.isBlank()) {
                    table.setItems(FXCollections.observableArrayList(
                        students.stream()
                            .filter(s -> s.getName().toLowerCase().contains(q))
                            .collect(Collectors.toList())
                    ));
                }
                int shown = table.getItems().size();
                countBadge.setText(shown + " student" + (shown == 1 ? "" : "s"));
            }));
            loadTask.setOnFailed(ev -> Platform.runLater(() -> {
                loadSpinner.setVisible(false);
                loadBtn.setDisable(false);
                countBadge.setText("Error");
                showWarn(root, "Failed to load students: " +
                    loadTask.getException().getMessage());
            }));
            new Thread(loadTask).start();
        });

        // Live search filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String q = newVal.trim().toLowerCase();
            if (q.isBlank()) {
                table.setItems(tableData);
                countBadge.setText(tableData.size() + " students");
            } else {
                ObservableList<Student> filtered = FXCollections.observableArrayList(
                    tableData.stream()
                        .filter(s -> s.getName().toLowerCase().contains(q))
                        .collect(Collectors.toList())
                );
                table.setItems(filtered);
                countBadge.setText(filtered.size() + " shown");
            }
        });

        clearBtn.setOnAction(e -> {
            batchBox.setValue(null);
            branchBox.setValue(null);
            searchField.clear();
            tableData.clear();
            table.setItems(tableData);
            countBadge.setText("0 students");
        });

        // Export with FileChooser — actual download
        exportBtn.setOnAction(e -> {
            if (table.getItems().isEmpty()) {
                showWarn(root, "No students to export. Load students first.");
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Student List");
            String batch  = batchBox.getValue()  != null ? batchBox.getValue()  : "all";
            String branch = branchBox.getValue() != null ? branchBox.getValue() : "all";
            chooser.setInitialFileName("students_" + branch + "_" + batch + ".csv");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            Stage stage = (Stage) root.getScene().getWindow();
            File file = chooser.showSaveDialog(stage);
            if (file == null) return;

            List<Student> toExport = table.getItems()
                .stream().collect(Collectors.toList());
            try {
                new StudentExporter().exportToFile(toExport, file);
                showInfo(root, "✅  Exported " + toExport.size() + " students to:\n" + file.getName());
            } catch (Exception ex) {
                showWarn(root, "Export failed: " + ex.getMessage());
            }
        });

        return scene;
    }

    // ── UI helpers ────────────────────────────────────────────────
    private VBox fieldGroup(String label, javafx.scene.Node field) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        lbl.setTextFill(Color.web("#8899aa"));
        return new VBox(3, lbl, field);
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

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefHeight(36);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 0 16;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    private Button outlineButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(36);
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #6c757d;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 0 14;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    private TableCell<Student, String> plainCell(String hexColor, int size) {
        return new TableCell<>() {
            @Override protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                setText(val);
                setFont(Font.font("Arial", size));
                setTextFill(Color.web(hexColor));
            }
        };
    }

    private TableCell<Student, String> chipCell(String bg, String fg) {
        return new TableCell<>() {
            @Override protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(val);
                badge.setStyle(
                    "-fx-background-color: " + bg + ";" +
                    "-fx-text-fill: " + fg + ";" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 2 10;" +
                    "-fx-background-radius: 99;"
                );
                setGraphic(badge); setText(null);
            }
        };
    }

    private Label placeholder(String msg) {
        Label l = new Label(msg);
        l.setFont(Font.font("Arial", 13));
        l.setTextFill(Color.web("#8899aa"));
        return l;
    }

    private void showWarn(javafx.scene.Node parent, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("SkillSync"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(javafx.scene.Node parent, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("SkillSync"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}