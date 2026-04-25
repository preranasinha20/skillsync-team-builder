import java.util.Arrays;

import dao.ProjectDAO;
import model.Project;

public class TestProjectDAO {

    public static void main(String[] args) {

        ProjectDAO dao = new ProjectDAO();

        // CREATE PROJECT
        Project project = new Project(
                5, // existing user id
                "AI Study App",
                "Build an AI-based learning platform",
                4,
                2024,
                "AIML"
        );

        int id = dao.createProject(project);

        System.out.println("Project created with ID: " + id);

        // ADD SKILLS
        if (id != -1) {
            dao.addProjectSkills(id, Arrays.asList("Java", "MySQL", "AI"));
            System.out.println("Skills added successfully.");
        }
    }
}