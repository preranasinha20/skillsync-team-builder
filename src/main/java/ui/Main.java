package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.screens.PostProjectScreen;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Load your Post Project Screen
        PostProjectScreen screen = new PostProjectScreen();

        stage.setTitle("SkillSync");
        stage.setScene(screen.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}