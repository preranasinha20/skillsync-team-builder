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
import java.util.stream.Collectors;
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
    public static List<Team> getTopTeams(int projectId, int batch, String branch,
        int teamSize, int excludeUserId) {
Set<String> projectSkills = getProjectSkills(projectId);
Map<Integer, Set<String>> studentSkills = getStudentSkills();
List<Integer> users = getStudents(batch, branch);

// Remove the project owner — they're already on the team
users.removeIf(id -> id == excludeUserId);

// teamSize already has owner subtracted when called from ProjectDetailScreen
if (users.size() < teamSize || teamSize < 1) return new ArrayList<>();

users.sort((a, b) -> {
int scoreA = studentSkills.getOrDefault(a, new HashSet<>()).size();
int scoreB = studentSkills.getOrDefault(b, new HashSet<>()).size();
return Integer.compare(scoreB, scoreA);
});

// Snake draft into 3 teams
List<List<Integer>> buckets = new ArrayList<>();
for (int i = 0; i < 3; i++) buckets.add(new ArrayList<>());

int filled = 0;
for (int i = 0; i < users.size() && filled < 3 * teamSize; i++) {
int direction = (i / 3) % 2;
int idx = direction == 0 ? (i % 3) : (2 - i % 3);
if (buckets.get(idx).size() < teamSize) {
buckets.get(idx).add(users.get(i));
filled++;
}
}

List<Team> teams = new ArrayList<>();
for (List<Integer> bucket : buckets) {
if (bucket.size() == teamSize) {
double score = calculateTeamScore(projectSkills, bucket, studentSkills);
teams.add(new Team(projectId, bucket, score));
}
}
return teams;
}

// BACKWARD COMPATIBILITY
public static List<Team> getTopTeams(int projectId) {
return getTopTeams(projectId, 2024, "CSE", getTeamSize(projectId), -1);
}

public static List<Team> getTopTeams(int projectId, int batch, String branch, int teamSize) {
return getTopTeams(projectId, batch, branch, teamSize, -1);
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

    // Get all user names in one query
    public static Map<Integer, String> getAllUserNamesForTeams(List<Team> teams) {
        Map<Integer, String> names = new HashMap<>();
        if (teams.isEmpty()) return names;
        
        // Collect all unique member IDs
        Set<Integer> allIds = new HashSet<>();
        for (Team t : teams) allIds.addAll(t.getMembers());
        if (allIds.isEmpty()) return names;
        
        String placeholders = allIds.stream()
            .map(id -> "?")
            .collect(java.util.stream.Collectors.joining(","));
        
        String sql = "SELECT id, name FROM users WHERE id IN (" + placeholders + ")";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            int i = 1;
            for (int id : allIds) stmt.setInt(i++, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) names.put(rs.getInt("id"), rs.getString("name"));
        } catch (Exception e) {
            System.err.println("[SkillMatcher] getAllUserNamesForTeams failed: " + e.getMessage());
        }
        return names;
    }

    // Get all skills for all team members in one query
    public static Map<Integer, String> getAllSkillStringsForTeams(List<Team> teams) {
        Map<Integer, String> skillStrings = new HashMap<>();
        if (teams.isEmpty()) return skillStrings;
        
        Set<Integer> allIds = new HashSet<>();
        for (Team t : teams) allIds.addAll(t.getMembers());
        if (allIds.isEmpty()) return skillStrings;
        
        String placeholders = allIds.stream()
            .map(id -> "?")
            .collect(java.util.stream.Collectors.joining(","));
        
        String sql = "SELECT user_id, skill_name, proficiency FROM skills WHERE user_id IN (" + placeholders + ")";
        Map<Integer, List<String>> skillMap = new HashMap<>();
        
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            int i = 1;
            for (int id : allIds) stmt.setInt(i++, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int uid = rs.getInt("user_id");
                String skill = rs.getString("skill_name") + 
                            "(" + rs.getString("proficiency").charAt(0) + ")";
                skillMap.computeIfAbsent(uid, k -> new ArrayList<>()).add(skill);
            }
        } catch (Exception e) {
            System.err.println("[SkillMatcher] getAllSkillStringsForTeams failed: " + e.getMessage());
        }
        
        // Convert to display strings
        for (Map.Entry<Integer, List<String>> entry : skillMap.entrySet()) {
            skillStrings.put(entry.getKey(), String.join(", ", entry.getValue()));
        }
        return skillStrings;
    }
}