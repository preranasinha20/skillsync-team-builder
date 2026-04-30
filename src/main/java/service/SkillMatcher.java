package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import database.DBConnection;
import model.Team;

public class SkillMatcher {

    public static Set<String> getProjectSkills(int projectId) {
        Set<String> skills = new HashSet<>();

        String sql = "SELECT skill_name FROM project_skills WHERE project_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                skills.add(rs.getString("skill_name").toLowerCase().trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return skills;
    }

    public static Map<Integer, Set<String>> getStudentSkills() {
        Map<Integer, Set<String>> map = new HashMap<>();

        String sql = "SELECT user_id, skill_name FROM skills";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String skill = rs.getString("skill_name").toLowerCase().trim();

                map.putIfAbsent(userId, new HashSet<>());
                map.get(userId).add(skill);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static List<Integer> getStudents(int batch, String branch) {
        List<Integer> users = new ArrayList<>();

        String sql = "SELECT id FROM users WHERE role='STUDENT' AND batch=? AND LOWER(TRIM(branch))=LOWER(TRIM(?))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, batch);
            stmt.setString(2, branch);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(rs.getInt("id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static int getTeamSize(int projectId) {
        String sql = "SELECT team_size FROM projects WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("team_size");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 3;
    }

    public static double calculateTeamScore(Set<String> projectSkills,
                                            List<Integer> team,
                                            Map<Integer, Set<String>> studentSkills) {

        if (projectSkills.isEmpty()) return 0;

        Set<String> covered = new HashSet<>();

        for (int user : team) {
            covered.addAll(studentSkills.getOrDefault(user, new HashSet<>()));
        }

        int match = 0;

        for (String skill : projectSkills) {
            if (covered.contains(skill)) {
                match++;
            }
        }

        return (double) match / projectSkills.size() * 100;
    }

    // OPTIMIZED
    public static List<Team> getTopTeams(int projectId, int batch, String branch, int teamSize) {

        Set<String> projectSkills = getProjectSkills(projectId);
        Map<Integer, Set<String>> studentSkills = getStudentSkills();
        List<Integer> users = getStudents(batch, branch);

        List<Team> teams = new ArrayList<>();

        if (users.size() < teamSize) return teams;

        Collections.shuffle(users);

        for (int i = 0; i + teamSize <= users.size(); i += teamSize) {

            List<Integer> team = new ArrayList<>();

            for (int j = 0; j < teamSize; j++) {
                team.add(users.get(i + j));
            }

            double score = calculateTeamScore(projectSkills, team, studentSkills);

            teams.add(new Team(projectId, team, score));
        }

        teams.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return teams.size() > 3 ? teams.subList(0, 3) : teams;
    }

    // BACKWARD COMPATIBILITY
    public static List<Team> getTopTeams(int projectId) {
        return getTopTeams(projectId, 2024, "CSE", getTeamSize(projectId));
    }

    public static Map<Integer, String> getAllUserNames() {
        Map<Integer, String> names = new HashMap<>();

        String sql = "SELECT id, name FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.put(rs.getInt("id"), rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return names;
    }

    public static String getUserName(int userId) {
        String sql = "SELECT name FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown";
    }
}