package csc207.mcp.interface_adapter.mcpkit;

/**
 * Stores the UI state for the Mcpkit view.
 */
public class McpkitState {
    private final String message;
    private final boolean status;

    public McpkitState() {
        this("", false);
    }

    public McpkitState(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getStatus() {
        return this.status;
    }
}
