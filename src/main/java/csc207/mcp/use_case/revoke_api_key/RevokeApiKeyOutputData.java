package csc207.mcp.use_case.revoke_api_key;

import java.util.Objects;

/** Confirmed revocation, with no credential or hash. */
public final class RevokeApiKeyOutputData {
    private final String keyId;
    private final boolean alreadyRevoked;
    public RevokeApiKeyOutputData(String keyId, boolean alreadyRevoked) {
        this.keyId = Objects.requireNonNull(keyId);
        this.alreadyRevoked = alreadyRevoked;
    }
    public String getKeyId() { return keyId; }
    public boolean isAlreadyRevoked() { return alreadyRevoked; }
}
