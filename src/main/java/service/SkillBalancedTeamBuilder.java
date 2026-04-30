package service;

import java.util.ArrayList;
import java.util.List;

import dao.UserDAO;
import model.Student;
import model.Team;

public class SkillBalancedTeamBuilder extends TeamBuilder {

    public SkillBalancedTeamBuilder(int batch, String branch) {
        super(batch, branch);
    }

    @Override
    public List<Team> buildTeams(int projectId, int teamSize) {

        List<Student> students = UserDAO.getStudentsByBatchAndBranch(batch, branch);

        List<Team> teams = new ArrayList<>();

        // 🚨 FIX: ensure enough students
        if (students.size() < teamSize) {
            return teams;
        }

        int i = 0;

        while (i + teamSize <= students.size()) {

            List<Integer> members = new ArrayList<>();

            for (int j = 0; j < teamSize; j++) {
                members.add(students.get(i + j).getId());
            }

            // simple score placeholder
            teams.add(new Team(projectId, members, 100));

            i += teamSize;
        }

        return teams;
    }
}