package csc207.mcp.use_case.list_mcpkit;

import csc207.mcp.entity.McpServer;
import java.util.List;

public class ListMcpkitOutputData {
    private List<McpServer> mcpkit;
    private boolean status;
    private String message;

    public ListMcpkitOutputData(List<McpServer> mcpkit, boolean status, String message) {
        this.mcpkit = mcpkit;
        this.status = status;
        this.message = message;
    }

    public List<McpServer> getMcpkitList() {
        return mcpkit;
    }

    public boolean isSuccess() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
