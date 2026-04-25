package model;

import java.time.LocalDateTime;

public class Project {
    private int id;
    private int ownerId;
    private String title;
    private String description;
    private int teamSize;
    private int batch;
    private String branch;
    private ProjectStatus status;
    private LocalDateTime createdAt;

    // No-arg constructor — needed for mapResultSet
    public Project() {
        this.status = ProjectStatus.OPEN;
    }

    // Constructor for creating new projects
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
    public int getId()                  { return id; }
    public int getOwnerId()             { return ownerId; }
    public String getTitle()            { return title; }
    public String getDescription()      { return description; }
    public int getTeamSize()            { return teamSize; }
    public int getBatch()               { return batch; }
    public String getBranch()           { return branch; }
    public ProjectStatus getStatus()    { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Needed for getStatus() as String in UI
    public String getStatusString()     { return status != null ? status.name() : "OPEN"; }

    // Setters
    public void setId(int id)                       { this.id = id; }
    public void setOwnerId(int ownerId)             { this.ownerId = ownerId; }
    public void setTitle(String title)              { this.title = title; }
    public void setDescription(String description)  { this.description = description; }
    public void setTeamSize(int teamSize)           { this.teamSize = teamSize; }
    public void setBatch(int batch)                 { this.batch = batch; }
    public void setBranch(String branch)            { this.branch = branch; }
    public void setStatus(ProjectStatus status)     { this.status = status; }
    public void setCreatedAt(LocalDateTime time)    { this.createdAt = time; }

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