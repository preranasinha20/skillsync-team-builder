package model;

import java.time.LocalDateTime;

public abstract class User {

    protected int id;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String role;
    protected String branch;
    protected int batch;
    protected String bio;
    protected LocalDateTime createdAt;

    // ✅ FULL constructor (used by DAO)
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
        this.createdAt = createdAt;
    }

    // ✅ SIMPLER constructor (used in register etc.)
    public User(String name, String email, String passwordHash,
                String role, String branch, int batch) {

        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.branch = branch;
        this.batch = batch;
    }

    // ─── ABSTRACT ─────────────────────────────
    public abstract String getDashboardTitle();

    // ─── GETTERS ──────────────────────────────
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public String getBranch() { return branch; }
    public int getBatch() { return batch; }
    public String getBio() { return bio; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ✅ Important (for UI)
    public String getBatchString() {
        return String.valueOf(batch);
    }

    // ─── SETTERS ──────────────────────────────
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setBatch(int batch) { this.batch = batch; }
    public void setBio(String bio) { this.bio = bio; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}