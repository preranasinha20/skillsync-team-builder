package service;

import model.Team;
import java.util.List;

public abstract class TeamBuilder {

    protected int batch;
    protected String branch;

    public TeamBuilder(int batch, String branch) {
        this.batch = batch;
        this.branch = branch;
    }

    public abstract List<Team> buildTeams(String activityName, int teamSize, List<String> requiredSkills);
}