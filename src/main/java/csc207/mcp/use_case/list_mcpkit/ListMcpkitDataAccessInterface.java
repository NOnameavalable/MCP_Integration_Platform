package csc207.mcp.use_case.list_mcpkit;

import csc207.mcp.entity.McpServer;

public interface ListMcpkitDataAccessInterface {
    McpServer[] getMcpkitByUserId(String userId);
}
