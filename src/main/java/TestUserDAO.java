import dao.UserDAO;
import model.Student;
import model.Teacher;
import model.User;
import model.Skill;
import database.PasswordUtil;

import java.util.List;

public class TestUserDAO {

    public static void main(String[] args) {

        System.out.println("========== SKILLSYNC USER DAO TEST ==========\n");

        String uniqueEmail = "prerna" + System.currentTimeMillis() + "@test.com";

        // TEST 1: Register Student
        System.out.println("TEST 1 — Registering student...");
        Student student = new Student(
                "Prerana Sinha",
                uniqueEmail,
                PasswordUtil.hash("prerna123"),
                "AIML",
                2024
        );
        boolean registered = UserDAO.registerUser(student);
        System.out.println(registered
                ? "PASS — Student registered with ID: " + student.getId()
                : "FAIL — Registration failed");

        // TEST 2: Register Teacher
        System.out.println("\nTEST 2 — Registering teacher...");
        Teacher teacher = new Teacher(
                "Dr. Sharma",
                "teacher" + System.currentTimeMillis() + "@test.com",
                PasswordUtil.hash("sharma123"),
                "AIML",
                2024,
                "Computer Science"  // ← was missing, Teacher needs department
        );
        boolean teacherRegistered = UserDAO.registerUser(teacher);
        System.out.println(teacherRegistered
                ? "PASS — Teacher registered with ID: " + teacher.getId()
                : "FAIL — Teacher registration failed");

        // TEST 3: Email exists
        System.out.println("\nTEST 3 — Checking email...");
        boolean exists = UserDAO.emailExists(uniqueEmail);
        System.out.println(exists
                ? "PASS — Email exists"
                : "FAIL — Email not found");

        // TEST 4: Login
        System.out.println("\nTEST 4 — Login...");
        User loggedIn = UserDAO.loginUser(uniqueEmail, PasswordUtil.hash("prerna123"));
        System.out.println(loggedIn != null
                ? "PASS — Login success: " + loggedIn
                : "FAIL — Login failed");

        // TEST 5: Wrong password
        System.out.println("\nTEST 5 — Wrong password...");
        User wrongLogin = UserDAO.loginUser(uniqueEmail, PasswordUtil.hash("wrongpassword"));
        System.out.println(wrongLogin == null
                ? "PASS — Wrong password rejected"
                : "FAIL — Wrong password accepted");

        // TEST 6: Get user by ID
        System.out.println("\nTEST 6 — Fetch user...");
        if (loggedIn != null) {
            User fetched = UserDAO.getUserById(loggedIn.getId());
            System.out.println(fetched != null
                    ? "PASS — User fetched: " + fetched
                    : "FAIL — Fetch failed");
        } else {
            System.out.println("SKIP — Login failed in TEST 4");
        }

        // TEST 7: Add skills
        System.out.println("\nTEST 7 — Adding skills...");
        if (student.getId() > 0) {
            boolean s1 = UserDAO.addSkill(student.getId(), "Java", "EXPERT");
            boolean s2 = UserDAO.addSkill(student.getId(), "MySQL", "INTERMEDIATE");
            boolean s3 = UserDAO.addSkill(student.getId(), "JavaFX", "BEGINNER");
            System.out.println((s1 && s2 && s3)
                    ? "PASS — 3 skills added"
                    : "FAIL — Some skills not added");
        } else {
            System.out.println("SKIP — Student ID not available");
        }

        // TEST 8: Get skills
        System.out.println("\nTEST 8 — Fetch skills...");
        List<Skill> skills = UserDAO.getSkillsByUser(student.getId());
        if (!skills.isEmpty()) {
            System.out.println("PASS — Skills found:");
            for (Skill skill : skills) {
                System.out.println("       " + skill.getSkillName() + " (" + skill.getProficiency() + ")");
                //                              ↑ getSkillName() not getName()
            }
        } else {
            System.out.println("FAIL — No skills returned");
        }

        // TEST 9: Update profile
        System.out.println("\nTEST 9 — Update profile...");
        student.setBio("I love Java!");
        boolean updated = UserDAO.updateProfile(student);
        System.out.println(updated
                ? "PASS — Profile updated"
                : "FAIL — Update failed");

        // TEST 10: Batch query
        System.out.println("\nTEST 10 — Fetch students by batch + branch...");
        List<Student> students = UserDAO.getStudentsByBatchAndBranch(2024, "AIML");
        System.out.println(students.size() > 0
                ? "PASS — Students found: " + students.size()
                : "FAIL — No students found");

        System.out.println("\n========== DONE ==========");
    }
}