package database;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.sql.*;
import java.util.*;

public class DataSeeder {

    private static final String URL = "jdbc:mysql://maglev.proxy.rlwy.net:22152/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";
    private static final String USER = "root";
    private static final String PASSWORD = "xZCUnJHGNYYNIfeZmpzhIqOMzcsTuLmg";

    private static final String PASSWORD_HASH = "ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae";

    private static final String[] BRANCHES = {"AIML", "CSE", "ENTC"};

    private static final String[] SKILLS = {
            "Java", "Python", "MySQL", "JavaFX", "Machine Learning",
            "Deep Learning", "React", "HTML/CSS", "Data Structures",
            "Android Dev", "NLP", "Computer Vision"
    };

    private static final String[] PROFICIENCY = {"BEGINNER", "INTERMEDIATE", "EXPERT"};

    private static final String FILE_2023 = "src/main/resources/data/batch2023.xlsx";
    private static final String FILE_2024 = "src/main/resources/data/batch2024.xlsx";

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            processFile(conn, FILE_2023, 2023);
            processFile(conn, FILE_2024, 2024);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processFile(Connection conn, String filePath, int batch) {

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
    
            for (Row row : sheet) {
    
                // Skip rows until we hit actual student data
                // Real data rows have a numeric SR. No. in column 0
                Cell srCell = row.getCell(0);
                if (srCell == null) continue;
                if (srCell.getCellType() != CellType.NUMERIC) continue;
    
                Cell rollCell = row.getCell(1);
                Cell nameCell = row.getCell(2);
    
                if (rollCell == null || nameCell == null) continue;
    
                String roll = getCellValueAsString(rollCell).trim();
                String name = getCellValueAsString(nameCell).trim();
    
                if (roll.isEmpty() || name.isEmpty()) continue;
    
                // Skip if looks like a header
                if (name.equalsIgnoreCase("student name") ||
                    roll.equalsIgnoreCase("prn")) continue;
    
                String email = generateEmail(name, roll);
                String branch = getRandomBranch();
    
                if (emailExists(conn, email)) {
                    System.out.println("⏭ Skipped: " + email);
                    continue;
                }
    
                int userId = insertUser(conn, name, email, branch, batch);
    
                if (userId != -1) {
                    insertSkills(conn, userId);
                    System.out.println("Inserted: " + name + " (" + email + ")");
                } else {
                    System.out.println("Failed: " + name);
                }
            }
    
        } catch (Exception e) {
            System.out.println("Error processing file: " + filePath);
            e.printStackTrace();
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    private static String generateEmail(String name, String roll) {
        String firstName = name.split(" ")[0].toLowerCase();
        return firstName + "." + roll.toLowerCase() + "@skillsync.edu";
    }

    private static String getRandomBranch() {
        return BRANCHES[new Random().nextInt(BRANCHES.length)];
    }

    private static boolean emailExists(Connection conn, String email) throws SQLException {
        String query = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private static int insertUser(Connection conn, String name, String email, String branch, int batch) throws SQLException {

        String query = "INSERT INTO users (name, email, password_hash, role, branch, batch, bio) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PASSWORD_HASH);
            ps.setString(4, "STUDENT");
            ps.setString(5, branch);
            ps.setInt(6, batch);
            ps.setNull(7, Types.VARCHAR);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    private static void insertSkills(Connection conn, int userId) throws SQLException {

        Random rand = new Random();
        int numSkills = 2 + rand.nextInt(3);

        Set<String> selectedSkills = new HashSet<>();

        while (selectedSkills.size() < numSkills) {
            selectedSkills.add(SKILLS[rand.nextInt(SKILLS.length)]);
        }

        String query = "INSERT INTO skills (user_id, skill_name, proficiency) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            for (String skill : selectedSkills) {
                ps.setInt(1, userId);
                ps.setString(2, skill);
                ps.setString(3, PROFICIENCY[rand.nextInt(PROFICIENCY.length)]);
                ps.executeUpdate();
            }
        }
    }
}