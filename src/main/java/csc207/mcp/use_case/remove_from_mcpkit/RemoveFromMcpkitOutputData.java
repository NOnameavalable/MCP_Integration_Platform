package csc207.mcp.use_case.remove_from_mcpkit;

import java.util.List;

import csc207.mcp.entity.McpServer;

public class RemoveFromMcpkitOutputData {
    private final boolean success;
    private final String mcpId;
    private final String mcpName;
    private final String message;
    private final List<McpServer> mcplist;

    public RemoveFromMcpkitOutputData(boolean success, String mcpId, String mcpName, List<McpServer> mcplist,
            String message) {
        this.success = success;
        this.mcpId = mcpId;
        this.mcpName = mcpName;
        this.message = message;
        this.mcplist = mcplist;
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

    public List<McpServer> getMcpkitList() {
        return mcplist;
    }
}
