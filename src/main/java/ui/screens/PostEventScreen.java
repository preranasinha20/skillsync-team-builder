package ui.screens;

import java.util.List;

import dao.UserDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;
import service.StudentExporter;

public class PostEventScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField batchField = new TextField();
        batchField.setPromptText("Batch");

        TextField skillsField = new TextField();
        skillsField.setPromptText("Required skills (comma separated)");

        Button matchBtn = new Button("Find Matching Students");
        Button exportBtn = new Button("Export CSV");
        Button backBtn = new Button("← Back");

        ListView<String> results = new ListView<>();

        matchBtn.setOnAction(e -> {
            int batch = Integer.parseInt(batchField.getText());
            List<Student> students = UserDAO.getStudentsByBatchAndBranch(batch, "AIML");

            results.getItems().clear();

            for (Student s : students) {
                results.getItems().add(s.getName());
            }
        });

        exportBtn.setOnAction(e -> {
            try {
                // simple export of names
                new StudentExporter().exportNames(results.getItems());
                new Alert(Alert.AlertType.INFORMATION, "Exported event_matches.csv").showAndWait();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        backBtn.setOnAction(e -> {
            Stage s = (Stage) root.getScene().getWindow();
            s.setScene(new TeacherHomeScreen().getScene());
        });

        root.getChildren().addAll(batchField, skillsField, matchBtn, exportBtn, results, backBtn);
        return new Scene(root, 900, 600);
    }
}