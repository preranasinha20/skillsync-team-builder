package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import database.DBConnection;
import model.Project;

public class ProjectDAO {

    // CREATE PROJECT
    public int createProject(Project project) {
        String sql = """
            INSERT INTO projects 
            (owner_id, title, description, team_size, batch, branch, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, project.getOwnerId());
            stmt.setString(2, project.getTitle());
            stmt.setString(3, project.getDescription());
            stmt.setInt(4, project.getTeamSize());
            stmt.setInt(5, project.getBatch());
            stmt.setString(6, project.getBranch());
            stmt.setString(7, project.getStatus().name());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // ADD PROJECT SKILLS
    public void addProjectSkills(int projectId, List<String> skills) {

        String sql = "INSERT INTO project_skills (project_id, skill_name) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String skill : skills) {
                stmt.setInt(1, projectId);
                stmt.setString(2, skill);
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}