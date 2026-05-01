import java.util.List;
import java.util.Map;

import model.Team;
import service.SkillMatcher;

public class TestSkillMatcher {

    public static void main(String[] args) {

        int projectId = 1;

        // ✅ UPDATED CALL (matches new method)
        List<Team> teams = SkillMatcher.getTopTeams(projectId, 2024, "CSE", 4);

        Map<Integer, String> names = SkillMatcher.getAllUserNamesForTeams(teams);

        System.out.println("Top Suggested Teams:\n");

        for (Team t : teams) {

            System.out.println("Team Score: " + t.getMatchScore());

            for (int userId : t.getMembers()) {
                System.out.println(" - [" + userId + "] " + names.get(userId));
            }

            System.out.println();
        }
    }
}