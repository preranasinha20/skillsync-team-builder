package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TeacherHomeScreen {

    public Scene getScene() {

        VBox root = new VBox();

        // NAVBAR
        HBox navbar = new HBox();
        navbar.setPadding(new Insets(15));
        navbar.setStyle("-fx-background-color: #1a1a2e;");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Teacher Dashboard");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));

        navbar.getChildren().add(title);

        // CONTENT
        VBox content = new VBox(25);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #f0f2f5;");

        Button viewStudents = new Button("View Students");
        Button postEvent = new Button("Post Event");
        Button teamBuilder = new Button("Team Builder");

        styleButton(viewStudents);
        styleButton(postEvent);
        styleButton(teamBuilder);

        viewStudents.setOnAction(e -> {
            Stage s = (Stage) root.getScene().getWindow();
            s.setScene(new ViewStudentsScreen().getScene());
        });

        postEvent.setOnAction(e -> {
            Stage s = (Stage) root.getScene().getWindow();
            s.setScene(new PostEventScreen().getScene());
        });

        teamBuilder.setOnAction(e -> {
            Stage s = (Stage) root.getScene().getWindow();
            s.setScene(new TeamBuilderScreen().getScene());
        });

        content.getChildren().addAll(viewStudents, postEvent, teamBuilder);

        root.getChildren().addAll(navbar, content);
        return new Scene(root, 900, 600);
    }

    private void styleButton(Button b) {
        b.setPrefWidth(260);
        b.setPrefHeight(50);
        b.setStyle(
            "-fx-background-color: #e94560;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;"
        );
    }
}