package csc207.mcp.entity;

import java.util.List;

/**
 * Represents a single tool provided by an MCP server.
 */
public class Tool {
    private String name;
    private String description;
    private List<String> parameters;

    public Tool(String name, String description, List<String> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Tool))
            return false;
        Tool other = (Tool) obj;
        return this.name.equals(other.name);
    }

    // able to use hashset
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
