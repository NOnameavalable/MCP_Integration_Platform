package csc207.mcp.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an MCP server and all the tools it provides.
 */
public class McpServer extends Mcp {
    public McpServer(String name, String description) {
        this(name, description, UUID.randomUUID().toString());
    }

    public McpServer(String name, String description, String mcpId) {
        super(name, description, mcpId, List.of());
    }

    public void addTool(Tool tool) {
        List<Tool> tools = getTools();
        if (!tools.contains(tool)) {
            tools.add(tool);
        }
    }

    public boolean removeTool(String toolName) {
        List<Tool> tools = getTools();
        for (Tool t : tools) {
            if (t.getName().equals(toolName)) {
                tools.remove(t);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof McpServer))
            return false;
        McpServer other = (McpServer) obj;
        return Objects.equals(getMcpId(), other.getMcpId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMcpId());
    }
}
