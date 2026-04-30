package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Skill;
import model.Student;
import model.Teacher;
import model.User;

public class UserDAO {

    // ─── REGISTER ─────────────────────────────────────────
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, branch, batch, bio) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getBranch());
            stmt.setInt(6, user.getBatch()); // ✅ INT only
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

    // ─── LOGIN ────────────────────────────────────────────
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

    // ─── GET USER BY ID ───────────────────────────────────
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

    // ─── GET STUDENTS (FIXED + SAFE) ──────────────────────
    public static List<Student> getStudentsByBatchAndBranch(int batch, String branch) {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM users " +
                     "WHERE role = 'STUDENT' " +
                     "AND batch = ? " +
                     "AND LOWER(TRIM(branch)) = LOWER(TRIM(?))";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, batch); // ✅ correct
            stmt.setString(2, branch.trim());

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    User user = mapResultSetToUser(rs);

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

    // ─── UPDATE PROFILE ───────────────────────────────────
    public static boolean updateProfile(User user) {

        String sql = "UPDATE users SET name = ?, branch = ?, batch = ?, bio = ? WHERE id = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getBranch());
            stmt.setInt(3, user.getBatch()); // ✅ int only
            stmt.setString(4, user.getBio());
            stmt.setInt(5, user.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO] updateProfile failed: " + e.getMessage());
        }

        return false;
    }

    // ─── EMAIL CHECK ──────────────────────────────────────
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

    // ─── SKILLS ───────────────────────────────────────────
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

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    skills.add(new Skill(
                            rs.getInt("id"),
                            rs.getString("skill_name"),
                            rs.getString("proficiency")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserDAO] getSkillsByUser failed: " + e.getMessage());
        }

        return skills;
    }

    // ─── HELPER ───────────────────────────────────────────
    private static User mapResultSetToUser(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String passHash = rs.getString("password_hash");
        String role = rs.getString("role");
        String branch = rs.getString("branch");
        int batch = rs.getInt("batch"); // ✅ CRITICAL FIX
        String bio = rs.getString("bio");

        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

        if ("TEACHER".equalsIgnoreCase(role)) {
            return new Teacher(id, name, email, passHash, branch, batch, bio, createdAt, branch);
        } else {
            return new Student(id, name, email, passHash, branch, batch, bio, createdAt);
        }
    }
}