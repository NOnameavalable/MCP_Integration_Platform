package csc207.mcp.entity;

import java.util.ArrayList;
import java.util.List;

public class Mcpkit {
    private int userId;
    private List<McpServer> mcpServers;

    public Mcpkit(int userId) {
        this.userId = userId;
        this.mcpServers = new ArrayList<>();
    }

    public boolean addToolkit(McpServer mcpServer) {
        if (!mcpServers.contains(mcpServer)) {
            mcpServers.add(mcpServer);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeToolkit(McpServer mcpServer) {
        if (mcpServers.contains(mcpServer)) {
            mcpServers.remove(mcpServer);
            return true;
        } else {
            return false;
        }
    }

    public List<McpServer> toolKitGetter() {
        return mcpServers;
    }

    public int userIdGetter() {
        return userId;
    }

    public boolean contains(McpServer mcpServer) {
        return mcpServers.contains(mcpServer);
    }
}
