package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Event;

public class EventDAO {

    // ── POST EVENT ───────────────────────────────────────────────
    public static boolean createEvent(Event event) {
        String sql = """
            INSERT INTO events (teacher_id, title, description, required_skills,
                                event_date, target_batch, target_branch)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, event.getTeacherId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setString(4, event.getRequiredSkills());

            stmt.setDate(5, event.getEventDate() != null
                    ? Date.valueOf(event.getEventDate()) : null);

            // ✅ FIX: batch handling
            if (event.getTargetBatch() > 0) {
                stmt.setInt(6, event.getTargetBatch());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, event.getTargetBranch());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) event.setId(keys.getInt(1));
                return true;
            }

        } catch (Exception e) {
            System.err.println("[EventDAO] createEvent failed: " + e.getMessage());
        }

        return false;
    }

    // ── GET EVENTS FOR STUDENT ───────────────────────────────────
    public static List<Event> getEventsForStudent(int batch, String branch) {

        List<Event> events = new ArrayList<>();

        String sql = """
            SELECT * FROM events
            WHERE (target_batch IS NULL OR target_batch = ?)
              AND (target_branch IS NULL OR LOWER(TRIM(target_branch)) = LOWER(TRIM(?)))
            ORDER BY created_at DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, batch);
            stmt.setString(2, branch.trim());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(mapRow(rs));
            }

        } catch (Exception e) {
            System.err.println("[EventDAO] getEventsForStudent failed: " + e.getMessage());
        }

        return events;
    }

    // ── GET EVENTS BY TEACHER ────────────────────────────────────
    public static List<Event> getEventsByTeacher(int teacherId) {

        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events WHERE teacher_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(mapRow(rs));
            }

        } catch (Exception e) {
            System.err.println("[EventDAO] getEventsByTeacher failed: " + e.getMessage());
        }

        return events;
    }

    // ── 🔥 FIXED: GET DISTINCT BATCHES ────────────────────────────
    public static List<String> getDistinctBatches() {

        List<String> batches = new ArrayList<>();

        String sql = """
            SELECT DISTINCT batch
            FROM users
            WHERE role = 'STUDENT' AND batch IS NOT NULL
            ORDER BY batch DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int batch = rs.getInt("batch");   // ✅ FORCE INT
                batches.add(String.valueOf(batch)); // ✅ CLEAN STRING
            }

        } catch (Exception e) {
            System.err.println("[EventDAO] getDistinctBatches failed: " + e.getMessage());
        }

        return batches;
    }

    // ── GET DISTINCT BRANCHES ────────────────────────────────────
    public static List<String> getDistinctBranches() {

        List<String> branches = new ArrayList<>();

        String sql = """
            SELECT DISTINCT TRIM(branch) AS branch
            FROM users
            WHERE role = 'STUDENT' AND branch IS NOT NULL
            ORDER BY branch
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                branches.add(rs.getString("branch"));
            }

        } catch (Exception e) {
            System.err.println("[EventDAO] getDistinctBranches failed: " + e.getMessage());
        }

        return branches;
    }

    // ── MAPPER ───────────────────────────────────────────────────
    private static Event mapRow(ResultSet rs) throws SQLException {

        Event e = new Event();

        e.setId(rs.getInt("id"));
        e.setTeacherId(rs.getInt("teacher_id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setRequiredSkills(rs.getString("required_skills"));

        Date d = rs.getDate("event_date");
        if (d != null) {
            e.setEventDate(d.toLocalDate());
        }

        e.setTargetBatch(rs.getInt("target_batch")); // ✅ stays int
        e.setTargetBranch(rs.getString("target_branch"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            e.setCreatedAt(ts.toLocalDateTime());
        }

        return e;
    }
}