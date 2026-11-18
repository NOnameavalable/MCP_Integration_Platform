package csc207.mcp.use_case.remove_from_mcpkit;

public class RemoveFromMcpkitInputData {
    private final String userId;
    private final String mcpId;

    public RemoveFromMcpkitInputData(String userId, String mcpId) {
        this.userId = userId;
        this.mcpId = mcpId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMcpId() {
        return mcpId;
    }
}
