package database;

import java.sql.Connection;
import java.sql.Statement;

public class TeamSchema {

    public static void createTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // TEAM REQUESTS
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS team_requests (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    project_id INT NOT NULL,
                    sender_id INT NOT NULL,
                    receiver_id INT NOT NULL,
                    type ENUM('JOIN', 'INVITE') NOT NULL,
                    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
                    message TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE (project_id, sender_id, receiver_id, type)
                );
            """);

            // TEAM MEMBERS
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS team_members (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    project_id INT NOT NULL,
                    user_id INT NOT NULL,
                    role VARCHAR(100),
                    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE (project_id, user_id)
                );
            """);

            // EVENTS
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS events (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    teacher_id INT NOT NULL,
                    title VARCHAR(200) NOT NULL,
                    description TEXT,
                    required_skills TEXT,
                    event_date DATE,
                    target_batch YEAR,
                    target_branch VARCHAR(50),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
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
