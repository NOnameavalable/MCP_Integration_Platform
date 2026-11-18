package csc207.mcp.use_case.remove_from_mcpkit;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.User;

public interface RemoveFromMcpkitDataAccessInterface {
    User getUserById(String userId);

    McpServer getMcpById(String mcpId);

    void removeFromUserMcpkit(String userId, String mcpId);
}
