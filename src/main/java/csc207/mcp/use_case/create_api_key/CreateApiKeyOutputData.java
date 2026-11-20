package csc207.mcp.use_case.create_api_key;

import java.time.Instant;
import java.util.Objects;

/** Transient creation result. Display the raw secret once; never log or persist this DTO. */
public final class CreateApiKeyOutputData {
    private final String keyId;
    private final String displayPrefix;
    private final Instant createdAt;
    private final String rawKey;

    public CreateApiKeyOutputData(String keyId, String displayPrefix, Instant createdAt, String rawKey) {
        this.keyId = Objects.requireNonNull(keyId);
        this.displayPrefix = Objects.requireNonNull(displayPrefix);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.rawKey = Objects.requireNonNull(rawKey);
    }
    public String getKeyId() { return keyId; }
    public String getDisplayPrefix() { return displayPrefix; }
    public Instant getCreatedAt() { return createdAt; }
    public String getRawKey() { return rawKey; }
}
