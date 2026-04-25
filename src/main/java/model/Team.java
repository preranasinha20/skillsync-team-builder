package model;

import java.util.List;

public class Team {

    private int projectId;
    private List<Integer> members;
    private double matchScore;

    public Team(int projectId, List<Integer> members, double matchScore) {
        this.projectId = projectId;
        this.members = members;
        this.matchScore = matchScore;
    }

    public int getProjectId() { return projectId; }
    public List<Integer> getMembers() { return members; }
    public double getMatchScore() { return matchScore; }

    @Override
    public String toString() {
        return "Team{" +
                "projectId=" + projectId +
                ", members=" + members +
                ", matchScore=" + matchScore +
                '}';
    }
}