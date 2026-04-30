package ui.screens;

import java.time.LocalDate;
import java.util.List;

import dao.EventDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Event;
import model.User;
import ui.SessionManager;

public class PostEventScreen {

    public Scene getScene() {

        User teacher = SessionManager.getUser();

        HBox root = new HBox();
        root.getChildren().add(
            TeacherHomeScreen.buildSidebar("postevent", teacher)
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
        Text pageTitle = new Text("Post Event");
        pageTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        pageTitle.setFill(Color.web(TeacherHomeScreen.NAVY));
        Label subtitle = new Label("Create an event visible to specific batches or branches");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.web("#8899aa"));
        titleBox.getChildren().addAll(pageTitle, subtitle);
        topBar.getChildren().add(titleBox);

        // ── Two-column layout: form | my events ──────────────────
        HBox body = new HBox(24);
        body.setPadding(new Insets(28, 30, 28, 30));
        VBox.setVgrow(body, Priority.ALWAYS);

        // ── LEFT: event form ─────────────────────────────────────
        VBox formCard = new VBox(18);
        formCard.setPadding(new Insets(24));
        formCard.setPrefWidth(460);
        formCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 2);"
        );

        Text formTitle = new Text("New Event Details");
        formTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        formTitle.setFill(Color.web(TeacherHomeScreen.NAVY));

        // Event title
        TextField titleField = styledField("e.g. Hackathon 2025", 380);
        // Description
        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe the event, what to expect, schedule, etc.");
        descArea.setPrefHeight(90);
        descArea.setWrapText(true);
        descArea.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8 10;"
        );

        // Required skills
        TextField skillsField = styledField("e.g. Java, Python, AI  (comma separated)", 380);

        // Event date
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select event date");
        datePicker.setPrefWidth(380);
        datePicker.setPrefHeight(38);
        datePicker.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );

        // Target batch — dropdown from DB
        ComboBox<String> batchBox = new ComboBox<>();
        batchBox.setPromptText("All Batches  (leave blank for everyone)");
        batchBox.setPrefWidth(380);
        batchBox.setPrefHeight(38);
        styleCombo(batchBox);

        // Target branch — dropdown from DB
        ComboBox<String> branchBox = new ComboBox<>();
        branchBox.setPromptText("All Branches  (leave blank for everyone)");
        branchBox.setPrefWidth(380);
        branchBox.setPrefHeight(38);
        styleCombo(branchBox);

        // Status label
        Label statusLbl = new Label("");
        statusLbl.setFont(Font.font("Arial", 12));
        statusLbl.setWrapText(true);

        // Submit button
        Button submitBtn = new Button("📅  Post Event");
        submitBtn.setPrefHeight(42);
        submitBtn.setPrefWidth(380);
        submitBtn.setStyle(
            "-fx-background-color: " + TeacherHomeScreen.CRIMSON + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        // Audience preview chip
        Label audienceChip = new Label("📢  Visible to: All students");
        audienceChip.setStyle(
            "-fx-background-color: #edfbf5;" +
            "-fx-text-fill: #2ec08a;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 4 12;" +
            "-fx-background-radius: 99;"
        );

        // Update audience chip live
        Runnable updateChip = () -> {
            String b  = batchBox.getValue();
            String br = branchBox.getValue();
            String audience = "All students";
            if (b != null && br != null) audience = "Batch " + b + " · " + br + " only";
            else if (b  != null) audience = "Batch " + b + " (all branches)";
            else if (br != null) audience = br + " branch (all batches)";
            audienceChip.setText("📢  Visible to: " + audience);
        };
        batchBox.setOnAction(e -> updateChip.run());
        branchBox.setOnAction(e -> updateChip.run());

        formCard.getChildren().addAll(
            formTitle,
            fieldGroup("Event Title *", titleField),
            fieldGroup("Description", descArea),
            fieldGroup("Required Skills", skillsField),
            fieldGroup("Event Date", datePicker),
            fieldGroup("Target Batch", batchBox),
            fieldGroup("Target Branch", branchBox),
            audienceChip,
            statusLbl,
            submitBtn
        );

        // ── RIGHT: my recent events list ─────────────────────────
        VBox rightPanel = new VBox(14);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        Text myEventsTitle = new Text("My Posted Events");
        myEventsTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        myEventsTitle.setFill(Color.web(TeacherHomeScreen.NAVY));

        VBox eventsList = new VBox(10);
        Label loadingLbl = new Label("Loading…");
        loadingLbl.setFont(Font.font("Arial", 12));
        loadingLbl.setTextFill(Color.web("#aaaaaa"));
        eventsList.getChildren().add(loadingLbl);

        rightPanel.getChildren().addAll(myEventsTitle, eventsList);

        body.getChildren().addAll(formCard, rightPanel);

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background-color: " + TeacherHomeScreen.BG + ";" +
            "-fx-background: " + TeacherHomeScreen.BG + ";"
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        main.getChildren().addAll(topBar, scroll);
        root.getChildren().add(main);

        // ── Load dropdowns + events async ────────────────────────
        Task<Object[]> initTask = new Task<>() {
            @Override protected Object[] call() {
                List<String> batches  = EventDAO.getDistinctBatches();
                List<String> branches = EventDAO.getDistinctBranches();
                List<model.Event> events = teacher != null
                    ? EventDAO.getEventsByTeacher(teacher.getId())
                    : List.of();
                return new Object[]{batches, branches, events};
            }
        };
        initTask.setOnSucceeded(e -> Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            List<String> batches  = (List<String>) initTask.getValue()[0];
            @SuppressWarnings("unchecked")
            List<String> branches = (List<String>) initTask.getValue()[1];
            @SuppressWarnings("unchecked")
            List<model.Event> events = (List<model.Event>) initTask.getValue()[2];

            batchBox.getItems().addAll(batches);
            branchBox.getItems().addAll(branches);

            renderEventsList(eventsList, events);
        }));
        new Thread(initTask).start();

        // ── Submit handler ────────────────────────────────────────
        submitBtn.setOnAction(e -> {
            String evTitle = titleField.getText().trim();
            if (evTitle.isEmpty()) {
                setStatus(statusLbl, "⚠  Event title is required.", false);
                return;
            }

            submitBtn.setDisable(true);
            submitBtn.setText("Posting…");

            String skills    = skillsField.getText().trim();
            String batchStr  = batchBox.getValue();
            String branchStr = branchBox.getValue();
            LocalDate date   = datePicker.getValue();
            String desc      = descArea.getText().trim();

            int batchVal = 0;
            if (batchStr != null && !batchStr.isBlank()) {
                try { batchVal = Integer.parseInt(batchStr); } catch (Exception ignored) {}
            }

            int finalBatch = batchVal;
            Task<Boolean> saveTask = new Task<>() {
                @Override protected Boolean call() {
                    Event event = new Event(
                        teacher != null ? teacher.getId() : 0,
                        evTitle, desc, skills, date,
                        finalBatch,
                        (branchStr != null && !branchStr.isBlank()) ? branchStr : null
                    );
                    return EventDAO.createEvent(event);
                }
            };
            saveTask.setOnSucceeded(ev -> Platform.runLater(() -> {
                submitBtn.setDisable(false);
                submitBtn.setText("📅  Post Event");
                if (saveTask.getValue()) {
                    setStatus(statusLbl, "✅  Event posted successfully!", true);
                    titleField.clear();
                    descArea.clear();
                    skillsField.clear();
                    datePicker.setValue(null);
                    batchBox.setValue(null);
                    branchBox.setValue(null);
                    audienceChip.setText("📢  Visible to: All students");

                    // Refresh right panel
                    if (teacher != null) {
                        Task<List<Event>> refresh = new Task<>() {
                            @Override protected List<Event> call() {
                                return EventDAO.getEventsByTeacher(teacher.getId());
                            }
                        };
                        refresh.setOnSucceeded(r -> Platform.runLater(() ->
                            renderEventsList(eventsList, refresh.getValue())));
                        new Thread(refresh).start();
                    }
                } else {
                    setStatus(statusLbl, "❌  Failed to post event. Try again.", false);
                }
            }));
            saveTask.setOnFailed(ev -> Platform.runLater(() -> {
                submitBtn.setDisable(false);
                submitBtn.setText("📅  Post Event");
                setStatus(statusLbl, "❌  Error: " + saveTask.getException().getMessage(), false);
            }));
            new Thread(saveTask).start();
        });

        return new Scene(root, 1100, 660);
    }

    // ── Render events list ────────────────────────────────────────
    private void renderEventsList(VBox container, List<Event> events) {
        container.getChildren().clear();
        if (events.isEmpty()) {
            Label empty = new Label("No events posted yet.");
            empty.setFont(Font.font("Arial", 13));
            empty.setTextFill(Color.web("#aaaaaa"));
            container.getChildren().add(empty);
            return;
        }
        for (Event evt : events) {
            container.getChildren().add(buildEventCard(evt));
        }
    }

    private VBox buildEventCard(Event evt) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 1);"
        );

        HBox row1 = new HBox(8);
        row1.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label("📅");
        iconLbl.setFont(Font.font(15));
        Label titleLbl = new Label(evt.getTitle());
        titleLbl.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        titleLbl.setTextFill(Color.web(TeacherHomeScreen.NAVY));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        String dateStr = evt.getEventDate() != null
            ? evt.getEventDate().toString() : "No date";
        Label dateLbl = new Label(dateStr);
        dateLbl.setStyle(
            "-fx-background-color: #eef3ff;" +
            "-fx-text-fill: #4f8ef7;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 2 8;" +
            "-fx-background-radius: 99;"
        );
        row1.getChildren().addAll(iconLbl, titleLbl, sp, dateLbl);

        String audience =
            (evt.getTargetBatch() > 0 ? "Batch " + evt.getTargetBatch() : "All Batches")
            + "  ·  "
            + (evt.getTargetBranch() != null && !evt.getTargetBranch().isBlank()
                ? evt.getTargetBranch() : "All Branches");

        Label audienceLbl = new Label(audience);
        audienceLbl.setFont(Font.font("Arial", 11));
        audienceLbl.setTextFill(Color.web("#8899aa"));

        if (evt.getDescription() != null && !evt.getDescription().isBlank()) {
            Label descLbl = new Label(evt.getDescription().length() > 80
                ? evt.getDescription().substring(0, 80) + "…"
                : evt.getDescription());
            descLbl.setFont(Font.font("Arial", 11));
            descLbl.setTextFill(Color.web("#6c757d"));
            card.getChildren().addAll(row1, audienceLbl, descLbl);
        } else {
            card.getChildren().addAll(row1, audienceLbl);
        }

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

    private void setStatus(Label lbl, String msg, boolean success) {
        lbl.setText(msg);
        lbl.setTextFill(success ? Color.web("#2ec08a") : Color.web(TeacherHomeScreen.CRIMSON));
    }
}