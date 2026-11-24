package csc207.mcp.entity;

import java.util.ArrayList;
import java.util.List;

/** Catalog information shared by marketplace loading and search. */
public class Mcp {
    private final String name;
    private final String description;
    private final String mcpId;
    private final List<Tool> tools;

    public Mcp(String name, String description, String mcpId, List<Tool> tools) {
        this.name = name;
        this.description = description;
        this.mcpId = mcpId;
        this.tools = new ArrayList<>(tools);
    }

    public String getMcpName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMcpId() {
        return mcpId;
    }

    /** Returns the live list of tools provided by this server. */
    public List<Tool> getTools() {
        return tools;
    }
}
