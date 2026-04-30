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
    public List<Team> buildTeams(int projectId, int teamSize) {

        List<Student> students = UserDAO.getStudentsByBatchAndBranch(batch, branch);
        if (students.isEmpty() || teamSize < 1) return new ArrayList<>();

        // Score each student by total skill weight
        Map<Integer, Integer> scoreMap = new HashMap<>();
        for (Student s : students) {
            List<Skill> skills = UserDAO.getSkillsByUser(s.getId());
            int score = 0;
            for (Skill sk : skills) {
                score += switch (sk.getProficiency()) {
                    case "EXPERT"       -> 3;
                    case "INTERMEDIATE" -> 2;
                    default             -> 1;
                };
            }
            scoreMap.put(s.getId(), score);
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
                teams.add(new Team(projectId, bucket, avg));
            }
        }

        return teams;
    }
}