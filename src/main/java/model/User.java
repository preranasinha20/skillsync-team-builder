package model;

import java.time.LocalDateTime;

public abstract class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private String branch;
    private int batch;
    private String bio;
    private LocalDateTime createdAt; // now properly set when loading from DB

    // Constructor for new registration — no id, no createdAt (DB handles it)
    public User(String name, String email, String passwordHash, String role, String branch, int batch) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.branch = branch;
        this.batch = batch;
    }

    // Constructor for loading from DB — includes id and createdAt
    public User(int id, String name, String email, String passwordHash,
                String role, String branch, int batch, String bio, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.branch = branch;
        this.batch = batch;
        this.bio = bio;
        this.createdAt = createdAt; // properly set now
    }

    public abstract String getDashboardTitle();

    // Getters
    public int getId()                  { return id; }
    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public String getPasswordHash()     { return passwordHash; }
    public String getRole()             { return role; }
    public String getBranch()           { return branch; }
    public int getBatch()               { return batch; }
    public String getBio()              { return bio; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id)                       { this.id = id; }
    public void setName(String name)                { this.name = name; }
    public void setEmail(String email)              { this.email = email; }
    public void setPasswordHash(String hash)        { this.passwordHash = hash; }
    public void setBranch(String branch)            { this.branch = branch; }
    public void setBatch(int batch)                 { this.batch = batch; }
    public void setBio(String bio)                  { this.bio = bio; }
    public void setCreatedAt(LocalDateTime time)    { this.createdAt = time; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', role='" + role + 
               "', branch='" + branch + "', batch=" + batch + ", createdAt=" + createdAt + "}";
    }
}