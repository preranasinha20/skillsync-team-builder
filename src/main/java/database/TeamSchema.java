package database;

import java.sql.Connection;
import java.sql.Statement;

public class TeamSchema {

    public static void createTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS team_requests (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    project_id INT,
                    sender_id INT,
                    receiver_id INT,
                    status VARCHAR(50)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS team_members (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    project_id INT,
                    user_id INT,
                    role VARCHAR(50)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS events (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(100),
                    description TEXT,
                    event_date DATE
                );
            """);

            System.out.println("Chandana tables created successfully.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}