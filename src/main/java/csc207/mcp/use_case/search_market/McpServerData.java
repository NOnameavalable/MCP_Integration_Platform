package csc207.mcp.use_case.search_market;

import java.util.List;

/** Immutable server search data returned by the data-access adapter. */
public final class McpServerData {
    private final String mcpId;
    private final String name;
    private final String description;
    private final List<String> toolNames;

    /** Use the server's existing ID and an empty toolNames list when it has no tools. */
    public McpServerData(String mcpId, String name, String description, List<String> toolNames) {
        this.mcpId = mcpId;
        this.name = name;
        this.description = description;
        this.toolNames = List.copyOf(toolNames);
    }

    public String getMcpId() { return mcpId; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getToolNames() {
        return toolNames;
    }
}
