package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import model.Team;

public class CSVExporter implements Exportable {

    @Override
    public void export(List<Team> teams, int projectId) throws Exception {

        BufferedWriter writer =
                new BufferedWriter(new FileWriter("teams_project_" + projectId + ".csv"));

        writer.write("Project ID,Members\n");

        for (Team t : teams) {

            StringBuilder names = new StringBuilder();

            for (int id : t.getMembers()) {
                names.append(SkillMatcher.getUserName(id)).append(" | ");
            }

            writer.write(projectId + "," + names + "\n");
        }

        writer.close();
    }
}