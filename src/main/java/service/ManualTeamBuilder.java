package service;

import java.util.ArrayList;
import java.util.List;

import dao.UserDAO;
import model.Student;
import model.Team;

public class ManualTeamBuilder extends TeamBuilder {

    public ManualTeamBuilder(int batch, String branch) {
        super(batch, branch);
    }

    @Override
    public List<Team> buildTeams(int projectId, int teamSize) {

        List<Student> students = UserDAO.getStudentsByBatchAndBranch(batch, branch);
        List<Team> teams = new ArrayList<>();

        if (students.size() < teamSize) {
            return teams;
        }

        int i = 0;

        while (i + teamSize <= students.size()) {

            List<Integer> members = new ArrayList<>();

            for (int j = 0; j < teamSize; j++) {
                members.add(students.get(i + j).getId());
            }

            teams.add(new Team(projectId, members, 0));

            i += teamSize;
        }

        return teams;
    }
}