package model;

public class Project {

    private int id;
    private int ownerId;
    private String title;
    private String description;
    private int teamSize;
    private int batch;
    private String branch;
    private ProjectStatus status;

    // Constructor
    public Project(int ownerId, String title, String description,
                   int teamSize, int batch, String branch) {

        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.teamSize = teamSize;
        this.batch = batch;
        this.branch = branch;
        this.status = ProjectStatus.OPEN;
    }

    // Getters
    public int getId() { return id; }
    public int getOwnerId() { return ownerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTeamSize() { return teamSize; }
    public int getBatch() { return batch; }
    public String getBranch() { return branch; }
    public ProjectStatus getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Project{id=" + id +
                ", title='" + title + '\'' +
                ", teamSize=" + teamSize +
                ", batch=" + batch +
                ", branch='" + branch + '\'' +
                ", status=" + status +
                '}';
    }
}