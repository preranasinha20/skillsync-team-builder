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

        showDashboard(); // START HERE

        stage.setTitle("SkillSync");
        stage.show();
    }

    public static void showDashboard() {
        DashboardScreen screen = new DashboardScreen();
        primaryStage.setScene(screen.getScene());
    }

    public static void showPostProject() {
        PostProjectScreen screen = new PostProjectScreen();
        primaryStage.setScene(screen.getScene());
    }

    public static void showInbox() {
        InboxScreen screen = new InboxScreen();
        primaryStage.setScene(screen.getScene());
    }

    public static void main(String[] args) {
        launch();
    }
}