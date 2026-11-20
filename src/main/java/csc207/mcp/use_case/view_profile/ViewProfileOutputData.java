package csc207.mcp.use_case.view_profile;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Safe immutable profile result. No User or ApiKey entity crosses this boundary. */
public final class ViewProfileOutputData {
    private final boolean successful;
    private final String errorMessage;
    private final String username;
    private final String email;
    private final List<ApiKeySummary> keys;
    public ViewProfileOutputData(String username, String email, List<ApiKeySummary> keys) {
        this.successful = true;
        this.errorMessage = "";
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.keys = List.copyOf(keys);
    }
    private ViewProfileOutputData(String errorMessage) {
        this.successful = false;
        this.errorMessage = Objects.requireNonNull(errorMessage);
        this.username = "";
        this.email = "";
        this.keys = List.of();
    }
    public static ViewProfileOutputData failure(String errorMessage) { return new ViewProfileOutputData(errorMessage); }
    public boolean isSuccessful() { return successful; }
    public String getErrorMessage() { return errorMessage; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public List<ApiKeySummary> getKeys() { return keys; }

    /** Non-secret metadata only. */
    public record ApiKeySummary(String keyId, String displayPrefix, Instant createdAt,
            Instant lastUsedAt, Instant revokedAt) {
        public ApiKeySummary {
            Objects.requireNonNull(keyId);
            Objects.requireNonNull(displayPrefix);
            Objects.requireNonNull(createdAt);
        }
        public boolean isActive() { return revokedAt == null; }
    }
}
