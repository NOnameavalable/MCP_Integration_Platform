package csc207.mcp.use_case.list_mcpkit;

import csc207.mcp.entity.McpServer;
import java.util.List;

public interface ListMcpkitDataAccessInterface {
    List<McpServer> getMcpkitByUserId(String userId);
}
