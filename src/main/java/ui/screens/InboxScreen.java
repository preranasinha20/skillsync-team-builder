package ui.screens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.TeamRequestDAO;
import database.DBConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import ui.SessionManager;

public class InboxScreen {

    public Scene getScene() {

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5;");

        Label title = new Label("📥 Inbox");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

        Button backBtn = new Button("← Back to Home");
        backBtn.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 14;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> {
            javafx.stage.Stage stage = ui.Main.getStage();
            new ui.HomeFeedScreen(stage).show();
        });

root.getChildren().addAll(title, backBtn);

        VBox list = new VBox(10);

        int userId = SessionManager.getUser().getId();

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM team_requests WHERE receiver_id = ? AND status = 'PENDING'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                int id = rs.getInt("id");
                int projectId = rs.getInt("project_id");
                int senderId = rs.getInt("sender_id");
                String type = rs.getString("type");

                list.getChildren().add(buildCard(id, projectId, senderId, type));
            }

            if (!hasData) {
                Label empty = new Label("No requests yet");
                empty.setStyle("-fx-text-fill: #6c757d;");
                list.getChildren().add(empty);
            }

        } catch (Exception e) {
            Label error = new Label("Error loading requests");
            error.setStyle("-fx-text-fill: red;");
            list.getChildren().add(error);
            e.printStackTrace();
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);

        root.getChildren().add(scroll);

        return new Scene(root, 700, 500);
    }

    private VBox buildCard(int requestId, int projectId, int senderId, String type) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #dee2e6;" +
                "-fx-border-radius: 8;"
        );

        Label info = new Label(
                "Project: " + projectId +
                " | From User: " + senderId +
                " | Type: " + type
        );

        Button accept = new Button("Accept");
        Button reject = new Button("Reject");

        accept.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        reject.setStyle("-fx-background-color: #e94560; -fx-text-fill: white;");

        Label status = new Label();

accept.setOnAction(e -> {
    TeamRequestDAO.acceptRequest(requestId);
    status.setText("Accepted ✔");
});

reject.setOnAction(e -> {
    TeamRequestDAO.rejectRequest(requestId);
    status.setText("Rejected ✖");
});

        card.getChildren().addAll(info, accept, reject, status);

        return card;
    }
}