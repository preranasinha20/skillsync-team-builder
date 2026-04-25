package ui.screens;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Team;
import service.CSVExporter;
import service.ManualTeamBuilder;
import service.SkillBalancedTeamBuilder;
import service.SkillMatcher;
import service.TeamBuilder;

public class TeamBuilderScreen {

    private List<Team> currentTeams;

    public Scene getScene() {

        VBox root = new VBox();

        // ===== NAVBAR =====
        HBox navbar = new HBox();
        navbar.setPadding(new Insets(15));
        navbar.setStyle("-fx-background-color: #1a1a2e;");
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setSpacing(10);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        Text title = new Text("Team Builder");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));

        navbar.getChildren().addAll(backBtn, title);

        // ===== CONTENT =====
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f0f2f5;");

        // ===== FORM =====
        VBox form = new VBox(10);

        Label projectLabel = new Label("Project ID");
        TextField projectIdField = new TextField();
        projectIdField.setPromptText("Enter project ID (e.g., 1)");

        Label batchLabel = new Label("Batch");
        TextField batchField = new TextField();
        batchField.setPromptText("Enter batch (e.g., 2024)");

        Label branchLabel = new Label("Branch");
        TextField branchField = new TextField();
        branchField.setPromptText("Enter branch (e.g., AIML)");

        Label teamSizeLabel = new Label("Team Size");
        TextField teamSizeField = new TextField();
        teamSizeField.setPromptText("Enter team size (e.g., 3)");

        Label strategyLabel = new Label("Team Strategy");
        ComboBox<String> strategyBox = new ComboBox<>();
        strategyBox.getItems().addAll("Skill Balanced", "Manual");
        strategyBox.setValue("Skill Balanced");

        form.getChildren().addAll(
                projectLabel, projectIdField,
                batchLabel, batchField,
                branchLabel, branchField,
                teamSizeLabel, teamSizeField,
                strategyLabel, strategyBox
        );

        Button generateBtn = new Button("Generate Teams");
        stylePrimaryButton(generateBtn);

        Button exportBtn = new Button("Export CSV");
        styleSecondaryButton(exportBtn);

        VBox resultBox = new VBox(12);

        // ===== GENERATE =====
        generateBtn.setOnAction(e -> {

            resultBox.getChildren().clear();

            if (projectIdField.getText().isEmpty() ||
                batchField.getText().isEmpty() ||
                branchField.getText().isEmpty() ||
                teamSizeField.getText().isEmpty()) {

                resultBox.getChildren().add(new Label("Please fill all fields."));
                return;
            }

            int projectId;
            int batch;
            int teamSize;

            try {
                projectId = Integer.parseInt(projectIdField.getText());
                batch = Integer.parseInt(batchField.getText());
                teamSize = Integer.parseInt(teamSizeField.getText());
            } catch (Exception ex) {
                resultBox.getChildren().add(new Label("Project ID, Batch, and Team Size must be numbers."));
                return;
            }

            String branch = branchField.getText().trim();

            TeamBuilder builder;

            if ("Manual".equals(strategyBox.getValue())) {
                builder = new ManualTeamBuilder(batch, branch);
            } else {
                builder = new SkillBalancedTeamBuilder(batch, branch);
            }

            currentTeams = builder.buildTeams(projectId, teamSize);

            if (currentTeams.isEmpty()) {
                resultBox.getChildren().add(new Label("No teams could be generated."));
                return;
            }

            for (Team t : currentTeams) {

                VBox card = new VBox(6);
                card.setPadding(new Insets(10));
                card.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #ddd;"
                );

                Text score = new Text("Team Score: " + t.getMatchScore());
                score.setStyle("-fx-fill: #e94560; -fx-font-weight: bold;");

                card.getChildren().add(score);

                for (int id : t.getMembers()) {
                    card.getChildren().add(new Label("• " + SkillMatcher.getUserName(id)));
                }

                resultBox.getChildren().add(card);
            }
        });

        // ===== EXPORT =====
        exportBtn.setOnAction(e -> {

            if (currentTeams == null || currentTeams.isEmpty()) {
                return;
            }

            try {
                int projectId = Integer.parseInt(projectIdField.getText());
                new CSVExporter().export(currentTeams, projectId);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Complete");
                alert.setHeaderText(null);
                alert.setContentText("Teams saved as teams_project_" + projectId + ".csv");
                alert.showAndWait();

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText(null);
                alert.setContentText("Something went wrong while exporting.");
                alert.showAndWait();
            }
        });

        // ===== NAVIGATION =====
        backBtn.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new TeacherHomeScreen().getScene());
        });

        content.getChildren().addAll(
                form,
                generateBtn,
                exportBtn,
                resultBox
        );

        root.getChildren().addAll(navbar, content);

        return new Scene(root, 900, 600);
    }

    private void stylePrimaryButton(Button btn) {
        btn.setPrefHeight(40);
        btn.setStyle(
                "-fx-background-color: #e94560;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;"
        );
    }

    private void styleSecondaryButton(Button btn) {
        btn.setPrefHeight(40);
        btn.setStyle(
                "-fx-background-color: #1a1a2e;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;"
        );
    }
}