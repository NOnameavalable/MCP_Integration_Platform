package csc207.mcp.use_case.create_api_key;

/** Request identity must be supplied from trusted login state, never an editable owner field. */
public final class CreateApiKeyInputData {
    private final String userId;
    public CreateApiKeyInputData(String userId) { this.userId = userId; }
    public String getUserId() { return userId; }
}
