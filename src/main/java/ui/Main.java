package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.screens.DashboardScreen;
import ui.screens.InboxScreen;
import ui.screens.PostProjectScreen;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Start from login screen
        new LoginScreen(stage).show();

        stage.setTitle("SkillSync");
        stage.show();
    }

    // Navigation methods (your system - keep these)
    public static void showDashboard() {
        DashboardScreen screen = new DashboardScreen();
        primaryStage.setScene(screen.getScene());
    }

    public static void showPostProject() {
        PostProjectScreen screen = new PostProjectScreen(primaryStage);
        primaryStage.setScene(screen.getScene());
    }

    public static void showInbox() {
        InboxScreen screen = new InboxScreen();
        primaryStage.setScene(screen.getScene());
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}