package csc207.mcp.entity;

import java.util.ArrayList;
import java.util.List;

public class Mcpkit {
    private String userId;
    private List<McpServer> mcpServers;

    public Mcpkit(String userId) {
        this.userId = userId;
        this.mcpServers = new ArrayList<>();
    }

    public boolean addTool(McpServer mcpServer) {
        if (!mcpServers.contains(mcpServer)) {
            mcpServers.add(mcpServer);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeTool(McpServer mcpServer) {
        if (mcpServers.contains(mcpServer)) {
            mcpServers.remove(mcpServer);
            return true;
        } else {
            return false;
        }
    }

    public List<McpServer> mcpkitGetter() {
        return mcpServers;
    }

    public String userIdGetter() {
        return userId;
    }

    public boolean contains(McpServer mcpServer) {
        return mcpServers.contains(mcpServer);
    }
}
