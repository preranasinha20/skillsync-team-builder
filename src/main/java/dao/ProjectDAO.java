package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import database.DBConnection;

public class ProjectDAO {

    // 🔹 1. INSERT PROJECT
    public static boolean addProject(int ownerId, String title, String description,
                                     String status, String branch, int batch,
                                     int teamSize, Date deadline) {

        String query = "INSERT INTO projects (owner_id, title, description, status, branch, batch, team_size, deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, ownerId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, status);
            ps.setString(5, branch);
            ps.setInt(6, batch);
            ps.setInt(7, teamSize);
            ps.setDate(8, deadline);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error inserting project: " + e.getMessage());
            return false;
        }
    }

    // 🔹 2. GET ALL PROJECTS
    public static void getAllProjects() {

        String query = "SELECT * FROM projects";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Team Size: " + rs.getInt("team_size"));
                System.out.println("------------------------");
            }

        } catch (Exception e) {
            System.out.println("Error fetching projects: " + e.getMessage());
        }
    }
}