package csc207.mcp.use_case.remove_from_mcpkit;

import csc207.mcp.entity.McpServer;
import java.util.List;

/** Data operations required to remove an MCP server from a user's mcpkit. */
public interface RemoveFromMcpkitDataAccessInterface {
    boolean userExists(String userId);

    McpServer getMcpById(String mcpId);

    List<McpServer> getMcpkitByUserId(String userId);

    void removeFromUserMcpkit(String userId, String mcpId);
}
