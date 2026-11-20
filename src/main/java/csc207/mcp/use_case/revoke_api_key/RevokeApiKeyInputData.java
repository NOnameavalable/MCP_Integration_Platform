package csc207.mcp.use_case.revoke_api_key;

/** Only the selected non-secret record ID comes from the view. */
public final class RevokeApiKeyInputData {
    private final String keyId;
    public RevokeApiKeyInputData(String keyId) { this.keyId = keyId; }
    public String getKeyId() { return keyId; }
}
