package csc207.mcp.use_case.remove_from_mcpkit;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.User;
import java.util.List;

public interface RemoveFromMcpkitDataAccessInterface {
    User getUserById(String userId);

    McpServer getMcpById(String mcpId);

    List<McpServer> getMcpkit(String userId);

    void removeFromUserMcpkit(String userId, String mcpId);
}
