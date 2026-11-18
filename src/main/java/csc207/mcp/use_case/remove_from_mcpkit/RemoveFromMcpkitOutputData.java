package csc207.mcp.use_case.remove_from_mcpkit;

public class RemoveFromMcpkitOutputData {
    private final boolean success;
    private final String mcpId;
    private final String mcpName;
    private final String message;

    public RemoveFromMcpkitOutputData(boolean success, String mcpId, String mcpName, String message) {
        this.success = success;
        this.mcpId = mcpId;
        this.mcpName = mcpName;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMcpId() {
        return mcpId;
    }

    public String getMcpName() {
        return mcpName;
    }

    public String getMessage() {
        return message;
    }
}
