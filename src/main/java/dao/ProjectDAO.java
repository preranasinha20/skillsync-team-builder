package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DBConnection;
import model.Project;
import model.ProjectStatus;

public class ProjectDAO {

    // ── CREATE PROJECT ───────────────────────────────────────────
    public static int createProject(Project project) {
        String sql = "INSERT INTO projects (owner_id, title, description, team_size, batch, branch, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, project.getOwnerId());
            stmt.setString(2, project.getTitle());
            stmt.setString(3, project.getDescription());
            stmt.setInt(4, project.getTeamSize());
            stmt.setInt(5, project.getBatch());
            stmt.setString(6, project.getBranch());
            stmt.setString(7, project.getStatus().name());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[ProjectDAO] createProject failed: " + e.getMessage());
        }
        return -1;
    }

    // ── ADD PROJECT SKILLS ───────────────────────────────────────
    public static void addProjectSkills(int projectId, List<String> skills) {
        String sql = "INSERT INTO project_skills (project_id, skill_name) VALUES (?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            for (String skill : skills) {
                stmt.setInt(1, projectId);
                stmt.setString(2, skill);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (Exception e) {
            System.err.println("[ProjectDAO] addProjectSkills failed: " + e.getMessage());
        }
    }

    // ── GET ALL OPEN PROJECTS ────────────────────────────────────
    public static List<Project> getAllOpenProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE status = 'OPEN' ORDER BY created_at DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) projects.add(mapResultSet(rs));
        } catch (Exception e) {
            System.err.println("[ProjectDAO] getAllOpenProjects failed: " + e.getMessage());
        }
        return projects;
    }

    // ── GET OPEN PROJECTS BY BATCH AND BRANCH ───────────────────
    public static List<Project> getOpenProjectsByBatchAndBranch(int batch, String branch) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE status = 'OPEN'" +
                     (branch != null ? " AND branch = ?" : "") +
                     (batch > 0    ? " AND batch = ?"  : "") +
                     " ORDER BY created_at DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            int i = 1;
            if (branch != null) stmt.setString(i++, branch);
            if (batch > 0)      stmt.setInt(i++, batch);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) projects.add(mapResultSet(rs));
        } catch (Exception e) {
            System.err.println("[ProjectDAO] getOpenProjectsByBatchAndBranch failed: " + e.getMessage());
        }
        return projects;
    }

    // ── GET PROJECT BY ID ────────────────────────────────────────
    public static Project getProjectById(int projectId) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (Exception e) {
            System.err.println("[ProjectDAO] getProjectById failed: " + e.getMessage());
        }
        return null;
    }

    // ── GET PROJECTS BY OWNER ────────────────────────────────────
    public static List<Project> getProjectsByOwner(int ownerId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE owner_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) projects.add(mapResultSet(rs));
        } catch (Exception e) {
            System.err.println("[ProjectDAO] getProjectsByOwner failed: " + e.getMessage());
        }
        return projects;
    }

    // ── GET PROJECTS BY STATUS ───────────────────────────────────
    public static List<Project> getProjectsByStatus(String status) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE status = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) projects.add(mapResultSet(rs));
        } catch (Exception e) {
            System.err.println("[ProjectDAO] getProjectsByStatus failed: " + e.getMessage());
        }
        return projects;
    }

    // ── UPDATE PROJECT STATUS ────────────────────────────────────
    public static boolean updateStatus(int projectId, ProjectStatus status) {
        String sql = "UPDATE projects SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, projectId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[ProjectDAO] updateStatus failed: " + e.getMessage());
        }
        return false;
    }

    // ── GET PROJECT SKILLS ───────────────────────────────────────
    public static List<String> getProjectSkills(int projectId) {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM project_skills WHERE project_id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) skills.add(rs.getString("skill_name"));
        } catch (Exception e) {
            System.err.println("[ProjectDAO] getProjectSkills failed: " + e.getMessage());
        }
        return skills;
    }

    // ── PRIVATE HELPER ───────────────────────────────────────────
    private static Project mapResultSet(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setId(rs.getInt("id"));
        p.setOwnerId(rs.getInt("owner_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setTeamSize(rs.getInt("team_size"));
        p.setBatch(rs.getInt("batch"));
        p.setBranch(rs.getString("branch"));
        p.setStatus(ProjectStatus.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) p.setCreatedAt(ts.toLocalDateTime());
        return p;
    }
}