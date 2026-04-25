package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import database.DBConnection;
import model.Team;

public class SkillMatcher {

    // -------------------------
    // Get project skills
    // -------------------------
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

    // -------------------------
    // Get student skills
    // -------------------------
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

    // -------------------------
    // Get all students
    // -------------------------
    public static List<Integer> getAllStudents() {

        List<Integer> users = new ArrayList<>();

        String sql = "SELECT id FROM users WHERE role = 'STUDENT'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(rs.getInt("id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    // -------------------------
    // Get team size
    // -------------------------
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

    // -------------------------
    // Generate combinations
    // -------------------------
    public static void generateTeams(List<Integer> users, int size,
                                     int index, List<Integer> current,
                                     List<List<Integer>> result) {

        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = index; i < users.size(); i++) {
            current.add(users.get(i));
            generateTeams(users, size, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // -------------------------
    // Calculate score
    // -------------------------
    public static double calculateTeamScore(Set<String> projectSkills,
                                            List<Integer> team,
                                            Map<Integer, Set<String>> studentSkills) {

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

    // -------------------------
    // Get all user names in one go (optimized)
    // -------------------------
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

    // -------------------------
    // MAIN FUNCTION
    // -------------------------
    public static List<Team> getTopTeams(int projectId) {

        Set<String> projectSkills = getProjectSkills(projectId);
        Map<Integer, Set<String>> studentSkills = getStudentSkills();
        List<Integer> users = getAllStudents();

        int teamSize = getTeamSize(projectId);

        if (users.size() < teamSize) {
            System.out.println("Not enough students.");
            return new ArrayList<>();
        }

        List<List<Integer>> allTeams = new ArrayList<>();
        generateTeams(users, teamSize, 0, new ArrayList<>(), allTeams);

        PriorityQueue<Team> pq =
                new PriorityQueue<>((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        for (List<Integer> team : allTeams) {
            double score = calculateTeamScore(projectSkills, team, studentSkills);
            pq.add(new Team(projectId, team, score));
        }

        List<Team> topTeams = new ArrayList<>();

        for (int i = 0; i < 3 && !pq.isEmpty(); i++) {
            topTeams.add(pq.poll());
        }

        return topTeams;
    }
}