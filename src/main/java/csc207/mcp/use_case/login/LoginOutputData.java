package csc207.mcp.use_case.login;

import csc207.mcp.entity.Mcpkit;

/**
 * Carries the authenticated user's account fields to login success handlers.
 */
public class LoginOutputData {
    private final String userId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Mcpkit mcpkit;

    public LoginOutputData(String userId, String username, String email,
                           String passwordHash, Mcpkit mcpkit) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.mcpkit = mcpkit;
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

    /** Returns the stored password hash for internal use; do not display or log it. */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** Returns the user's toolkit reference, not a copy. */
    public Mcpkit getMcpkit() {
        return mcpkit;
    }
}
