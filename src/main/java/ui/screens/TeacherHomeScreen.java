package ui.screens;

import java.util.List;

import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Skill;
import model.Student;

public class TeacherHomeScreen {

    public Scene getScene() {

        VBox root = new VBox();

        // ===== NAVBAR =====
        HBox navbar = new HBox();
        navbar.setPadding(new Insets(15));
        navbar.setStyle("-fx-background-color: #1a1a2e;");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Teacher Dashboard");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));

        navbar.getChildren().add(title);

        // ===== CONTENT =====
        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f0f2f5;");

        // INPUT FIELDS
        TextField batchField = new TextField();
        batchField.setPromptText("Enter Batch (e.g., 2026)");

        TextField branchField = new TextField();
        branchField.setPromptText("Enter Branch (e.g., CSE)");

        Button loadBtn = new Button("Load Students");
        stylePrimaryButton(loadBtn);

        Button teamBuilderBtn = new Button("Go to Team Builder");
        styleSecondaryButton(teamBuilderBtn);

        VBox studentList = new VBox(10);

        // ===== LOAD STUDENTS =====
        loadBtn.setOnAction(e -> {

            studentList.getChildren().clear();

            // Validation
            if (batchField.getText().isEmpty() || branchField.getText().isEmpty()) {
                studentList.getChildren().add(new Label("Enter batch and branch."));
                return;
            }

            int batch;
            try {
                batch = Integer.parseInt(batchField.getText());
            } catch (Exception ex) {
                studentList.getChildren().add(new Label("Batch must be a number."));
                return;
            }

            String branch = branchField.getText().trim();

            List<Student> students =
                    UserDAO.getStudentsByBatchAndBranch(batch, branch);

            if (students.isEmpty()) {
                studentList.getChildren().add(new Label("No students found."));
                return;
            }

            for (Student s : students) {

                VBox card = new VBox(5);
                card.setPadding(new Insets(10));
                card.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #ddd;"
                );

                Text name = new Text(s.getName());
                name.setStyle("-fx-font-weight: bold;");

                card.getChildren().add(name);

                List<Skill> skills = UserDAO.getSkillsByUser(s.getId());

                if (skills.isEmpty()) {
                    card.getChildren().add(new Label("No skills"));
                } else {
                    for (Skill skill : skills) {
                        card.getChildren().add(
                                new Label("• " + skill.getSkillName() + " (" + skill.getProficiency() + ")")
                        );
                    }
                }

                studentList.getChildren().add(card);
            }
        });

        // ===== NAVIGATION =====
        teamBuilderBtn.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new TeamBuilderScreen().getScene());
        });

        ScrollPane scroll = new ScrollPane(studentList);
        scroll.setFitToWidth(true);

        content.getChildren().addAll(
                batchField,
                branchField,
                loadBtn,
                teamBuilderBtn,
                scroll
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