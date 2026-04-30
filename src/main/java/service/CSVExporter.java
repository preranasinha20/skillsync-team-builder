package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import model.Team;

public class CSVExporter implements Exportable {

    /**
     * Legacy method — saves to working directory.
     * Kept for backward compatibility.
     */
    @Override
    public void export(List<Team> teams, int projectId) throws Exception {
        exportToFile(teams, projectId, new File("teams_project_" + projectId + ".csv"));
    }

    /**
     * New method — saves to a user-chosen File (via FileChooser).
     * Writes: Team Number, Member Names, Match Score
     */
    public void exportToFile(List<Team> teams, int projectId, File file) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("Project ID,Team #,Members,Match Score");
            writer.newLine();

            int teamNum = 1;
            for (Team t : teams) {

                StringBuilder names = new StringBuilder();
                for (int id : t.getMembers()) {
                    if (names.length() > 0) names.append(" | ");
                    names.append(SkillMatcher.getUserName(id));
                }

                String line = String.join(",",
                    String.valueOf(projectId),
                    String.valueOf(teamNum),
                    escapeCsv(names.toString()),
                    String.format("%.1f%%", t.getMatchScore())
                );
                writer.write(line);
                writer.newLine();
                teamNum++;
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}