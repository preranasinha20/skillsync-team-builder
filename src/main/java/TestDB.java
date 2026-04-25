import java.sql.Date;

import dao.ProjectDAO;

public class TestDB {
    public static void main(String[] args) {

        // 🔹 Insert test project
        boolean inserted = ProjectDAO.addProject(
                1,
                "AI Project",
                "Cool AI system",
                "OPEN",
                "AIML",
                2026,
                4,
                Date.valueOf("2026-12-31")
        );

        System.out.println("Inserted: " + inserted);

        // 🔹 Fetch projects
        ProjectDAO.getAllProjects();
    }
}