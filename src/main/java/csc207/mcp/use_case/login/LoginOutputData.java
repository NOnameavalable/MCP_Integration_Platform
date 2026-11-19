package csc207.mcp.use_case.login;

/**
 * Carries the authenticated user's identity without exposing password data.
 */
public class LoginOutputData {
    private final String userId;
    private final String username;

    public LoginOutputData(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
