package service;

import model.Team;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import model.Student;
import javafx.concurrent.Task;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.DBConnection;
import dao.UserDAO;

public abstract class TeamBuilder {

    // Cache loaded once when screen opens
    private Map<Integer, Integer> studentScoreCache = new HashMap<>();
    private List<Student> cachedStudents = new ArrayList<>();
    private String lastBranch = "";
    private int lastBatch = 0;

    protected int batch;
    protected String branch;

    public TeamBuilder(int batch, String branch) {
        this.batch = batch;
        this.branch = branch;
        this.lastBranch = branch;
        this.lastBatch = batch;
    }

        private void preloadStudents(int batch, String branch) {
            if (batch == lastBatch && branch.equals(lastBranch)) return; // already cached
            
            Task<Void> cacheTask = new Task<>() {
                @Override protected Void call() {
                    List<Student> students = UserDAO.getStudentsByBatchAndBranch(batch, branch);
                    Map<Integer, Integer> scores = new HashMap<>();
                    
                    String sql = "SELECT s.user_id, s.proficiency FROM skills s " +
                                "JOIN users u ON s.user_id = u.id " +
                                "WHERE u.role = 'STUDENT' AND u.batch = ? AND u.branch = ?";
                    try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
                        stmt.setInt(1, batch);
                        stmt.setString(2, branch);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            int uid = rs.getInt("user_id");
                            String prof = rs.getString("proficiency");
                            int points = switch (prof) {
                                case "EXPERT" -> 3;
                                case "INTERMEDIATE" -> 2;
                                default -> 1;
                            };
                            scores.merge(uid, points, Integer::sum);
                        }
                    } catch (Exception e) {
                        System.err.println("Cache load failed: " + e.getMessage());
                    }
                    
                    cachedStudents = students;
                    studentScoreCache = scores;
                    lastBatch = batch;
                    lastBranch = branch;
                    return null;
                }
            };
            new Thread(cacheTask).start();
        }
     
    public abstract List<Team> buildTeams(String activityName, int teamSize, List<String> requiredSkills);
}