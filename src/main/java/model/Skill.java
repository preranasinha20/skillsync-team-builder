package model;

public class Skill {

    private int id;
    private int userId;
    private String skillName;
    private String proficiency; // BEGINNER / INTERMEDIATE / EXPERT

    public Skill(int id, int userId, String skillName, String proficiency) {
        this.id = id;
        this.userId = userId;
        this.skillName = skillName;
        this.proficiency = proficiency;
    }

    // Constructor without id (for inserting new skill)
    public Skill(int userId, String skillName, String proficiency) {
        this.userId = userId;
        this.skillName = skillName;
        this.proficiency = proficiency;
    }

    public int getId()              { return id; }
    public int getUserId()          { return userId; }
    public String getSkillName()    { return skillName; }
    public String getProficiency()  { return proficiency; }

    public void setId(int id)                       { this.id = id; }
    public void setProficiency(String proficiency)  { this.proficiency = proficiency; }

    @Override
    public String toString() {
        return skillName + " (" + proficiency + ")";
    }
}