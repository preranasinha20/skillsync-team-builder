package model;

import java.time.LocalDateTime;

public class Teacher extends User {

    private String department; // separate field, not aliased from branch anymore

    // Constructor for new registration
    public Teacher(String name, String email, String passwordHash, 
                   String branch, int batch, String department) {
        super(name, email, passwordHash, "TEACHER", branch, batch);
        this.department = department;
    }

    // Constructor for loading from DB
    public Teacher(int id, String name, String email, String passwordHash,
                   String branch, int batch, String bio, 
                   LocalDateTime createdAt, String department) {
        super(id, name, email, passwordHash, "TEACHER", branch, batch, bio, createdAt);
        this.department = department;
    }

    @Override
    public String getDashboardTitle() {
        return "Teacher Dashboard — " + getName();
    }

    public String getDepartment()               { return department; }
    public void setDepartment(String dept)      { this.department = dept; }

    @Override
    public String toString() {
        return "Teacher{id=" + getId() + ", name='" + getName() + 
               "', department='" + department + "', createdAt=" + getCreatedAt() + "}";
    }
}