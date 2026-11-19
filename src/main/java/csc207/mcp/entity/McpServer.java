package csc207.mcp.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an MCP server and all the tools it provides.
 */
public class McpServer {
    private String name;
    private String description;
    private List<Tool> tools;
    private String mcpId;

    public McpServer(String name, String description) {
        this.name = name;
        this.description = description;
        this.tools = new ArrayList<>();
        this.mcpId = UUID.randomUUID().toString();
    }

    public String getMcpName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public String getMcpId() {
        return mcpId;
    }

    public void addTool(Tool tool) {
        if (!tools.contains(tool)) {
            tools.add(tool);
        }
    }

    public boolean removeTool(String toolName) {
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
        return this.mcpId.equals(other.mcpId);
    }

    @Override
    public int hashCode() {
        return mcpId.hashCode();
    }
}
