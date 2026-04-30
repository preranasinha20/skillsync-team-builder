package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event {

    private int id;
    private int teacherId;
    private String title;
    private String description;
    private String requiredSkills;
    private LocalDate eventDate;
    private int targetBatch;
    private String targetBranch;
    private LocalDateTime createdAt;

    public Event() {}

    public Event(int teacherId, String title, String description,
                 String requiredSkills, LocalDate eventDate,
                 int targetBatch, String targetBranch) {
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.eventDate = eventDate;
        this.targetBatch = targetBatch;
        this.targetBranch = targetBranch;
    }

    public int getId()                    { return id; }
    public int getTeacherId()             { return teacherId; }
    public String getTitle()              { return title; }
    public String getDescription()        { return description; }
    public String getRequiredSkills()     { return requiredSkills; }
    public LocalDate getEventDate()       { return eventDate; }
    public int getTargetBatch()           { return targetBatch; }
    public String getTargetBranch()       { return targetBranch; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    public void setId(int id)                         { this.id = id; }
    public void setTeacherId(int teacherId)           { this.teacherId = teacherId; }
    public void setTitle(String title)                { this.title = title; }
    public void setDescription(String desc)           { this.description = desc; }
    public void setRequiredSkills(String skills)      { this.requiredSkills = skills; }
    public void setEventDate(LocalDate date)          { this.eventDate = date; }
    public void setTargetBatch(int batch)             { this.targetBatch = batch; }
    public void setTargetBranch(String branch)        { this.targetBranch = branch; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Event{id=" + id + ", title='" + title + "', batch=" + targetBatch +
               ", branch='" + targetBranch + "'}";
    }
}
