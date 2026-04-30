package service;

import java.util.List;

import model.Team;

public class SkillBalancedTeamBuilder extends TeamBuilder {

    public SkillBalancedTeamBuilder(int batch, String branch) {
        super(batch, branch);
    }

    @Override
    public List<Team> buildTeams(int projectId, int teamSize) {
        return SkillMatcher.getTopTeams(projectId, batch, branch, teamSize);
    }
}