package csc207.mcp.entity;

import java.time.Instant;

/**
 * A revocable credential record owned by one user. Stores a verification hash
 * and a safe display prefix, never the full secret. Generation, hashing,
 * authentication, and persistence are responsibilities of other components.
 */
public final class ApiKey {
    private final String keyId;
    private final String ownerUserId;
    private final String keyHash;
    private final String displayPrefix;
    private final Instant createdAt;
    private Instant lastUsedAt;
    private Instant revokedAt;

    /** Creates an active key record that has not been used. */
    public ApiKey(String keyId, String ownerUserId, String keyHash,
                  String displayPrefix, Instant createdAt) {
        this(keyId, ownerUserId, keyHash, displayPrefix, createdAt, null, null);
    }

    /**
     * Restores a persisted record. Null lastUsedAt means never used;
     * null revokedAt means active. Historical times must be consistent.
     * Values are retained without normalization, including the hash encoding.
     *
     * @throws IllegalArgumentException for missing fields or inconsistent times
     */
    public ApiKey(String keyId, String ownerUserId, String keyHash,
                  String displayPrefix, Instant createdAt,
                  Instant lastUsedAt, Instant revokedAt) {
        this.keyId = requireNonBlank(keyId, "keyId");
        this.ownerUserId = requireNonBlank(ownerUserId, "ownerUserId");
        this.keyHash = requireNonBlank(keyHash, "keyHash");
        this.displayPrefix = requireNonBlank(displayPrefix, "displayPrefix");
        this.createdAt = requireTime(createdAt, "createdAt");
        checkNotBeforeCreation(lastUsedAt, "lastUsedAt");
        checkNotBeforeCreation(revokedAt, "revokedAt");
        if (lastUsedAt != null && revokedAt != null && lastUsedAt.isAfter(revokedAt)) {
            throw new IllegalArgumentException("lastUsedAt must not be after revokedAt");
        }
        this.lastUsedAt = lastUsedAt;
        this.revokedAt = revokedAt;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    /** For verification/persistence only; never include in profile output or logs. */
    public String getKeyHash() {
        return keyHash;
    }

    /** Returns only the safe prefix supplied by the key-creation use case. */
    public String getDisplayPrefix() {
        return displayPrefix;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Returns null if the key has never been used. */
    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    /** Returns null if the key has not been revoked. */
    public Instant getRevokedAt() {
        return revokedAt;
    }

    public boolean isActive() {
        return revokedAt == null;
    }

    /** A missing or different user identifier does not own this key. */
    public boolean belongsTo(String userId) {
        return ownerUserId.equals(userId);
    }

    /**
     * Records revocation without deleting the record. Repeated revocation keeps
     * the original revocation time. The caller must persist the updated record.
     */
    public void revoke(Instant at) {
        requireTime(at, "revocation time");
        if (!isActive()) {
            return;
        }
        checkNotBeforeCreation(at, "revocation time");
        if (lastUsedAt != null && at.isBefore(lastUsedAt)) {
            throw new IllegalArgumentException("revocation time must not be before lastUsedAt");
        }
        revokedAt = at;
    }

    /**
     * Records a newly authorized use. This does not validate a raw key or
     * authorize access to any tool. Revoked keys cannot authorize new uses.
     */
    public void markUsed(Instant at) {
        requireTime(at, "use time");
        if (!isActive()) {
            throw new IllegalStateException("A revoked key cannot authorize a new use");
        }
        checkNotBeforeCreation(at, "use time");
        // An older event must not move the recorded latest-use time backwards.
        if (lastUsedAt == null || at.isAfter(lastUsedAt)) {
            lastUsedAt = at;
        }
    }

    private void checkNotBeforeCreation(Instant at, String fieldName) {
        if (at != null && at.isBefore(createdAt)) {
            throw new IllegalArgumentException(fieldName + " must not be before createdAt");
        }
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static Instant requireTime(Instant at, String fieldName) {
        if (at == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return at;
    }
}
