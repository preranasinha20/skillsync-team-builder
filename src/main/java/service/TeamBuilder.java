package service;

import java.util.List;

import model.Team;

public abstract class TeamBuilder {

    protected int batch;
    protected String branch;

    public TeamBuilder(int batch, String branch) {
        this.batch = batch;
        this.branch = branch;
    }

    public abstract List<Team> buildTeams(int projectId, int teamSize);
}