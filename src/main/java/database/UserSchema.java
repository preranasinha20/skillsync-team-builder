package database;

import java.sql.Connection;
import java.sql.Statement;

public class UserSchema {

    public static void createTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // USERS TABLE (FULL VERSION)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    role ENUM('STUDENT', 'TEACHER') NOT NULL,
                    branch VARCHAR(50),
                    batch YEAR,
                    bio TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """);

            // SKILLS TABLE (FULL VERSION)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS skills (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    skill_name VARCHAR(100) NOT NULL,
                    proficiency ENUM('BEGINNER', 'INTERMEDIATE', 'EXPERT') NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
            """);

            System.out.println("Tables (users, skills) created successfully.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}