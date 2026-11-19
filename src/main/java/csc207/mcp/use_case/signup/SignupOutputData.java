package csc207.mcp.use_case.signup;

import java.util.Objects;

/** Safe registration result: contains neither a password nor a password hash. */
public final class SignupOutputData {
    private final String userId;
    private final String username;
    private final String email;

    public SignupOutputData(String userId, String username, String email) {
        this.userId = Objects.requireNonNull(userId);
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
