package csc207.mcp.interface_adapter.mcpkit;

import java.util.ArrayList;
import java.util.List;
import csc207.mcp.entity.McpServer;

/**
 * Stores the UI state for the Mcpkit view.
 */
public class McpkitState {
    private final String message;
    private final boolean status;
    private List<McpServer> mcpkitList;

    public McpkitState() {
        this("", false, new ArrayList<>());
    }

    public McpkitState(String message, boolean status, List<McpServer> mcpkitList) {
        this.message = message;
        this.status = status;
        this.mcpkitList = mcpkitList;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getStatus() {
        return this.status;
    }

    public List<McpServer> getMcpkitList() {
        return this.mcpkitList;
    }
}
