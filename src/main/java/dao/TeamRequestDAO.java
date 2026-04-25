package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DBConnection;

public class TeamRequestDAO {

    // =========================
    // SEND REQUEST (SAFE INSERT)
    // =========================
    public static boolean sendRequest(int projectId, int senderId, int receiverId, String type, String message) {

        String sql = """
            INSERT INTO team_requests (project_id, sender_id, receiver_id, type, message)
            SELECT ?, ?, ?, ?, ?
            WHERE NOT EXISTS (
                SELECT 1 FROM team_requests
                WHERE project_id=? AND sender_id=? AND receiver_id=? AND type=?
            )
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            stmt.setInt(2, senderId);
            stmt.setInt(3, receiverId);
            stmt.setString(4, type);
            stmt.setString(5, message);

            stmt.setInt(6, projectId);
            stmt.setInt(7, senderId);
            stmt.setInt(8, receiverId);
            stmt.setString(9, type);

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // =========================
    // ACCEPT REQUEST (SAFE INSERT)
    // =========================
    public static boolean acceptRequest(int requestId) {

        String updateRequest = "UPDATE team_requests SET status = 'ACCEPTED' WHERE id = ?";

        String insertMember = """
            INSERT INTO team_members (project_id, user_id, role)
            SELECT project_id, receiver_id, 'MEMBER'
            FROM team_requests
            WHERE id = ?
            AND NOT EXISTS (
                SELECT 1 FROM team_members
                WHERE project_id = team_requests.project_id
                AND user_id = team_requests.receiver_id
            )
        """;

        String countMembers = "SELECT COUNT(*) FROM team_members WHERE project_id = ?";
        String getTeamSize = "SELECT team_size FROM projects WHERE id = ?";
        String updateProject = "UPDATE projects SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            int projectId = -1;

            // 1️⃣ Accept request
            try (PreparedStatement stmt = conn.prepareStatement(updateRequest)) {
                stmt.setInt(1, requestId);
                stmt.executeUpdate();
            }

            // 2️⃣ Add member (safe)
            try (PreparedStatement stmt = conn.prepareStatement(insertMember)) {
                stmt.setInt(1, requestId);
                stmt.executeUpdate();
            }

            // 3️⃣ Get project id
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT project_id FROM team_requests WHERE id = ?")) {

                stmt.setInt(1, requestId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    projectId = rs.getInt(1);
                }
            }

            // 4️⃣ Count members
            int memberCount = 0;
            try (PreparedStatement stmt = conn.prepareStatement(countMembers)) {
                stmt.setInt(1, projectId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    memberCount = rs.getInt(1);
                }
            }

            // 5️⃣ Get team size
            int teamSize = 0;
            try (PreparedStatement stmt = conn.prepareStatement(getTeamSize)) {
                stmt.setInt(1, projectId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    teamSize = rs.getInt(1);
                }
            }

            // 6️⃣ Update project status
            String newStatus = (memberCount >= teamSize) ? "ONGOING" : "FORMING";

            try (PreparedStatement stmt = conn.prepareStatement(updateProject)) {
                stmt.setString(1, newStatus);
                stmt.setInt(2, projectId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // =========================
    // REJECT REQUEST
    // =========================
    public static boolean rejectRequest(int requestId) {

        String sql = "UPDATE team_requests SET status = 'REJECTED' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // =========================
    // GET INCOMING REQUESTS
    // =========================
    public static void getIncomingRequests(int userId) {

        String sql = """
            SELECT * FROM team_requests
            WHERE receiver_id = ? AND status = 'PENDING'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nIncoming Requests:");

            while (rs.next()) {
                System.out.println(
                        "Request ID: " + rs.getInt("id") +
                        " | Project: " + rs.getInt("project_id") +
                        " | From: " + rs.getInt("sender_id") +
                        " | Type: " + rs.getString("type")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =========================
    // GET SENT REQUESTS
    // =========================
    public static void getSentRequests(int userId) {

        String sql = """
            SELECT * FROM team_requests
            WHERE sender_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nSent Requests:");

            while (rs.next()) {
                System.out.println(
                        "Request ID: " + rs.getInt("id") +
                        " | Project: " + rs.getInt("project_id") +
                        " | To: " + rs.getInt("receiver_id") +
                        " | Status: " + rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}