package dao;

import model.Skill;
import model.Student;
import model.Teacher;
import model.User;
import database.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ─── REGISTER ──────────────────────────────────────────────────────────

    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, branch, batch, bio) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // try-with-resources — statement closes automatically even if exception thrown
        try (PreparedStatement stmt = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getBranch());
            stmt.setInt(6, user.getBatch());
            stmt.setString(7, user.getBio());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] registerUser failed: " + e.getMessage());
        }
        return false;
    }

    // ─── LOGIN ─────────────────────────────────────────────────────────────

    public static User loginUser(String email, String passwordHash) {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] loginUser failed: " + e.getMessage());
        }
        return null;
    }

    // ─── GET USER BY ID ────────────────────────────────────────────────────

    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getUserById failed: " + e.getMessage());
        }
        return null;
    }

    // ─── GET ALL STUDENTS IN A BATCH + BRANCH ─────────────────────────────

    public static List<Student> getStudentsByBatchAndBranch(int batch, String branch) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'STUDENT' AND batch = ? AND branch = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, batch);
            stmt.setString(2, branch);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    // safe cast — only fetching STUDENT role rows
                    if (user instanceof Student) {
                        students.add((Student) user);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getStudentsByBatchAndBranch failed: " + e.getMessage());
        }
        return students;
    }

    // ─── UPDATE PROFILE ────────────────────────────────────────────────────

    public static boolean updateProfile(User user) {
        String sql = "UPDATE users SET name = ?, branch = ?, batch = ?, bio = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getBranch());
            stmt.setInt(3, user.getBatch());
            stmt.setString(4, user.getBio());
            stmt.setInt(5, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateProfile failed: " + e.getMessage());
        }
        return false;
    }

    // ─── CHECK IF EMAIL EXISTS ─────────────────────────────────────────────

    public static boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] emailExists failed: " + e.getMessage());
        }
        return false;
    }

    // ─── SKILLS ────────────────────────────────────────────────────────────

    public static boolean addSkill(int userId, String skillName, String proficiency) {
        String sql = "INSERT INTO skills (user_id, skill_name, proficiency) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, skillName);
            stmt.setString(3, proficiency);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] addSkill failed: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteSkill(int skillId) {
        String sql = "DELETE FROM skills WHERE id = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, skillId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteSkill failed: " + e.getMessage());
        }
        return false;
    }

    public static List<Skill> getSkillsByUser(int userId) {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT id, skill_name, proficiency FROM skills WHERE user_id = ?";
    
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                skills.add(new Skill(
                        rs.getInt("id"),
                        rs.getString("skill_name"),
                        rs.getString("proficiency")
                ));
            }
    
        } catch (SQLException e) {
            System.err.println("[UserDAO] getSkillsByUser failed: " + e.getMessage());
        }
    
        return skills;
    }

    // ─── PRIVATE HELPER ────────────────────────────────────────────────────

    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id                  = rs.getInt("id");
        String name             = rs.getString("name");
        String email            = rs.getString("email");
        String passHash         = rs.getString("password_hash");
        String role             = rs.getString("role");
        String branch           = rs.getString("branch");
        int batch               = rs.getInt("batch");
        String bio              = rs.getString("bio");

        // createdAt properly loaded from DB now
        Timestamp ts            = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

        if ("TEACHER".equals(role)) {
            // department defaults to branch — teacher can update it later from profile
            return new Teacher(id, name, email, passHash, branch, batch, bio, createdAt, branch);
        } else {
            return new Student(id, name, email, passHash, branch, batch, bio, createdAt);
        }
    }
}