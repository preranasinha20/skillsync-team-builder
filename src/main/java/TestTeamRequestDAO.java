import dao.TeamRequestDAO;

public class TestTeamRequestDAO {

    public static void main(String[] args) {

        System.out.println("========== TEAM REQUEST DAO TEST ==========");

        // 1️⃣ SEND REQUEST (change receiver if needed)
        boolean sent = TeamRequestDAO.sendRequest(
                1,
                5,
                6,   // changed to avoid duplicate
                "INVITE",
                "Join my team"
        );

        System.out.println(sent ? "PASS → Request sent" : "SKIPPED → Already exists");


        // 2️⃣ ACCEPT REQUEST
        boolean accepted = TeamRequestDAO.acceptRequest(1);

        System.out.println(accepted ? "PASS → Accepted" : "Handled safely");


        // 3️⃣ REJECT REQUEST
        boolean rejected = TeamRequestDAO.rejectRequest(2);

        System.out.println(rejected ? "PASS → Rejected" : "Reject failed");


        // 4️⃣ VIEW REQUESTS
        TeamRequestDAO.getIncomingRequests(7);
        TeamRequestDAO.getSentRequests(5);

        System.out.println("\n========== DONE ==========");
    }
}