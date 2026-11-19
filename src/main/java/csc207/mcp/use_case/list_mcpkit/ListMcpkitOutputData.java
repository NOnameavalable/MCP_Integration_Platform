package csc207.mcp.use_case.list_mcpkit;

import csc207.mcp.entity.McpServer;
import java.util.List;

public class ListMcpkitOutputData {
    private List<McpServer> mcpkit;
    private boolean status;
    private String error;

    public ListMcpkitOutputData(List<McpServer> mcpkit, boolean status, String error) {
        this.mcpkit = mcpkit;
        this.status = status;
        this.error = error;
    }

    public List<McpServer> getMcpkit() {
        return mcpkit;
    }

    public boolean getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
