package csc207.mcp.use_case.login;

/**
 * Carries the authenticated user's ID, username, and email to login success handlers.
 */
public class LoginOutputData {
    private final String userId;
    private final String username;
    private final String email;

    public LoginOutputData(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
