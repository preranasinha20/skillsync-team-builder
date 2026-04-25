package database;

import java.sql.Connection;
import java.sql.Statement;

public class ProjectSchema {

    public static void createTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS projects (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    owner_id INT NOT NULL,
                    title VARCHAR(200) NOT NULL,
                    description TEXT,
                    status ENUM('OPEN', 'FORMING', 'ONGOING', 'COMPLETED') DEFAULT 'OPEN',
                    branch VARCHAR(50),
                    batch YEAR,
                    team_size INT NOT NULL,
                    deadline DATE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS project_skills (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    project_id INT NOT NULL,
                    skill_name VARCHAR(100) NOT NULL,
                    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                    UNIQUE (project_id, skill_name)
                );
            """);

            System.out.println("Manas tables created successfully.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}