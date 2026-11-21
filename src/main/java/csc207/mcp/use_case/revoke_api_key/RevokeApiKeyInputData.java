package csc207.mcp.use_case.revoke_api_key;

/** Request identity must be supplied from trusted login state, never an editable owner field. */
public final class RevokeApiKeyInputData {
    private final String userId;
    private final String keyId;
    public RevokeApiKeyInputData(String userId, String keyId) { this.userId = userId; this.keyId = keyId; }
    public String getUserId() { return userId; }
    public String getKeyId() { return keyId; }
}
