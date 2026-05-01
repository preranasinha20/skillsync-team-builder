package service;

import dao.UserDAO;
import model.Skill;
import model.Student;
import model.Team;

import java.util.*;

public class SkillBalancedTeamBuilder extends TeamBuilder {

    public SkillBalancedTeamBuilder(int batch, String branch) {
        super(batch, branch);
    }

    @Override
    public List<Team> buildTeams(String activityName, int teamSize, List<String> requiredSkills) {

        List<Student> students = UserDAO.getStudentsByBatchAndBranch(batch, branch);
        if (students.isEmpty() || teamSize < 1) return new ArrayList<>();

        // Score each student by total skill weight
        // ONE query to get all skills for all students in this batch/branch
        Map<Integer, Integer> scoreMap = new HashMap<>();
        String sql = "SELECT s.user_id, s.proficiency FROM skills s " +
                    "JOIN users u ON s.user_id = u.id " +
                    "WHERE u.role = 'STUDENT' AND u.batch = ? AND u.branch = ?";
        try (java.sql.PreparedStatement stmt = database.DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, batch);
            stmt.setString(2, branch);
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String prof = rs.getString("proficiency");
                int points = switch (prof) {
                    case "EXPERT"       -> 3;
                    case "INTERMEDIATE" -> 2;
                    default             -> 1;
                };
                scoreMap.merge(userId, points, Integer::sum);
            }
        } catch (Exception e) {
            System.err.println("[SkillBalancedTeamBuilder] scoring failed: " + e.getMessage());
        }

        // Sort by score descending
        students.sort((a, b) ->
            scoreMap.getOrDefault(b.getId(), 0) - scoreMap.getOrDefault(a.getId(), 0)
        );

        // Round-robin distribution into teams
        int numTeams = (int) Math.ceil((double) students.size() / teamSize);
        List<List<Integer>> buckets = new ArrayList<>();
        for (int i = 0; i < numTeams; i++) buckets.add(new ArrayList<>());

        for (int i = 0; i < students.size(); i++) {
            // snake pattern: 0,1,2,3,3,2,1,0,0,1...
            int direction = (i / numTeams) % 2;
            int idx = direction == 0 ? (i % numTeams) : (numTeams - 1 - i % numTeams);
            buckets.get(idx).add(students.get(i).getId());
        }

        // Convert buckets to Team objects
        List<Team> teams = new ArrayList<>();
        for (List<Integer> bucket : buckets) {
            if (!bucket.isEmpty()) {
                int total = bucket.stream().mapToInt(id -> scoreMap.getOrDefault(id, 0)).sum();
                double avg = (double) total / bucket.size();
                teams.add(new Team(0, bucket, avg));
            }
        }

        return teams;
    }
}