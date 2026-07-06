package com.researchpapers.utill;

public class SessionManager {

    public static String loggedUser = null;
    public static int currentUserId = -1;
    public static String loggedUserFullName = null;
    public static String loggedUserRole = null;

    public static void clear() {
        loggedUser = null;
        currentUserId = -1;
        loggedUserFullName = null;
        loggedUserRole = null;
    }
}
