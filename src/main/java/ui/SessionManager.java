package ui;

import model.User;

public class SessionManager {

    private static User loggedInUser;

    public static void setUser(User user) {
        loggedInUser = user;
    }

    public static User getUser() {
        return loggedInUser;
    }

    public static void clear() {
        loggedInUser = null;
    }
}