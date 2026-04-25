package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private List<Skill> skills; // fixed — full Skill objects not just names

    // Constructor for new registration
    public Student(String name, String email, String passwordHash, String branch, int batch) {
        super(name, email, passwordHash, "STUDENT", branch, batch);
        this.skills = new ArrayList<>();
    }

    // Constructor for loading from DB
    public Student(int id, String name, String email, String passwordHash,
                   String branch, int batch, String bio, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, "STUDENT", branch, batch, bio, createdAt);
        this.skills = new ArrayList<>();
    }

    @Override
    public String getDashboardTitle() {
        return "Student Dashboard — " + getName();
    }

    public List<Skill> getSkills()              { return skills; }
    public void setSkills(List<Skill> skills)   { this.skills = skills; }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    @Override
    public String toString() {
        return "Student{id=" + getId() + ", name='" + getName() + 
               "', branch='" + getBranch() + "', batch=" + getBatch() + 
               ", skills=" + skills + "}";
    }
}