package model;

import java.time.LocalDateTime;

public class TeamRequest {

    private int id;
    private int projectId;
    private int senderId;
    private int receiverId;
    private String type;
    private String status;
    private String message;
    private LocalDateTime createdAt;

    public TeamRequest(int projectId, int senderId, int receiverId, String type, String message) {
        this.projectId = projectId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.message = message;
    }

    public int getId() { return id; }
    public int getProjectId() { return projectId; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }

    public void setId(int id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "TeamRequest{id=" + id +
                ", project=" + projectId +
                ", sender=" + senderId +
                ", receiver=" + receiverId +
                ", status=" + status + "}";
    }
}