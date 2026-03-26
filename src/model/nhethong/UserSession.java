package model.nhethong;

public class UserSession {
    private static String loggedInId;    // Đây là username (Mã SV hoặc NV)
    private static String loggedInType;  // AD, SV, hoặc NV

    public static void saveSession(String id, String type) {
        loggedInId = id;
        loggedInType = type;
    }

    public static String getUsername() { return loggedInId; }
    public static String getUserType() { return loggedInType; }
    public static void clear() {
        loggedInId = null;
        loggedInType = null;
        System.out.println("Session has been cleared.");
    }
}