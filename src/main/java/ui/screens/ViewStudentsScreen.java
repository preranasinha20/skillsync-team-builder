package ui.screens;

import java.util.List;
import java.util.stream.Collectors;

import dao.UserDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Skill;
import model.Student;
import service.StudentExporter;

public class ViewStudentsScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        TextField batchField = new TextField();
        batchField.setPromptText("Batch");

        TextField branchField = new TextField();
        branchField.setPromptText("Branch");

        Button loadBtn = new Button("Load");
        Button exportBtn = new Button("Export CSV");
        Button backBtn = new Button("← Back");

        TableView<Student> table = new TableView<>();

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Student, String> skillCol = new TableColumn<>("Skills");
        skillCol.setCellValueFactory(d -> {
            List<Skill> skills = UserDAO.getSkillsByUser(d.getValue().getId());
            String s = skills.stream().map(Skill::getSkillName).collect(Collectors.joining(", "));
            return new SimpleStringProperty(s);
        });

        table.getColumns().addAll(nameCol, skillCol);

        loadBtn.setOnAction(e -> {
            int batch = Integer.parseInt(batchField.getText());
            String branch = branchField.getText();
            List<Student> list = UserDAO.getStudentsByBatchAndBranch(batch, branch);
            table.setItems(FXCollections.observableArrayList(list));
        });

        exportBtn.setOnAction(e -> {
            try {
                new StudentExporter().export(table.getItems());
                new Alert(Alert.AlertType.INFORMATION, "Exported students.csv").showAndWait();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        backBtn.setOnAction(e -> {
            Stage s = (Stage) root.getScene().getWindow();
            s.setScene(new TeacherHomeScreen().getScene());
        });

        root.getChildren().addAll(batchField, branchField, loadBtn, exportBtn, table, backBtn);
        return new Scene(root, 900, 600);
    }
}