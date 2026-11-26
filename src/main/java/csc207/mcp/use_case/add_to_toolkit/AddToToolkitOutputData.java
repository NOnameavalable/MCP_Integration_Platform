package csc207.mcp.use_case.add_to_toolkit;

import csc207.mcp.entity.McpServer;
import java.util.List;

/** Identifies the saved addition and carries the updated toolkit for presentation. */
public final class AddToToolkitOutputData {
    private final String userId;
    private final String serverId;
    private final String serverName;
    private final List<McpServer> mcpkitList;

    public AddToToolkitOutputData(String userId, String serverId, String serverName,
                                 List<McpServer> mcpkitList) {
        this.userId = userId;
        this.serverId = serverId;
        this.serverName = serverName;
        this.mcpkitList = List.copyOf(mcpkitList);
    }

    public String getUserId() { return userId; }
    public String getServerId() { return serverId; }
    public String getServerName() { return serverName; }
    public List<McpServer> getMcpkitList() { return mcpkitList; }
}
