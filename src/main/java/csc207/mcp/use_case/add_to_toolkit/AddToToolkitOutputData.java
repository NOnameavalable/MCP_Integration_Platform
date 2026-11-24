package csc207.mcp.use_case.add_to_toolkit;

/** Identifies the user and MCP server after a successful save. */
public final class AddToToolkitOutputData {
    private final String userId;
    private final String serverId;
    private final String serverName;

    public AddToToolkitOutputData(String userId, String serverId, String serverName) {
        this.userId = userId;
        this.serverId = serverId;
        this.serverName = serverName;
    }

    public String getUserId() { return userId; }
    public String getServerId() { return serverId; }
    public String getServerName() { return serverName; }
}
