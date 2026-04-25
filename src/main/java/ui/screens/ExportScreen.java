package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ExportScreen {

    public Scene getScene() {

        VBox root = new VBox();

        HBox navbar = new HBox();
        navbar.setPadding(new Insets(15));
        navbar.setStyle("-fx-background-color: #1a1a2e;");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        Text title = new Text("Export Teams");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));

        navbar.getChildren().addAll(backBtn, title);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f0f2f5;");

        Label label = new Label("Export feature ready!");
        label.setStyle("-fx-font-size: 14px;");

        backBtn.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new TeacherHomeScreen().getScene());
        });

        content.getChildren().add(label);
        root.getChildren().addAll(navbar, content);

        return new Scene(root, 900, 600);
    }
}