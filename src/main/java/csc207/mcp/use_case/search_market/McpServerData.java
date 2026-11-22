package csc207.mcp.use_case.search_market;

import java.util.List;

/** Immutable server search data returned by the data-access adapter. */
public final class McpServerData {
    private final String name;
    private final String description;
    private final List<String> toolNames;

    /** Supply an empty toolNames list when the server has no tools. */
    public McpServerData(String name, String description, List<String> toolNames) {
        this.name = name;
        this.description = description;
        this.toolNames = List.copyOf(toolNames);
    }

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
