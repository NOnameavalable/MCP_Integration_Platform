package csc207.mcp.use_case.add_to_toolkit;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.User;

/** Database operations required to add a marketplace server to a user's toolkit. */
public interface AddToToolkitDataAccessInterface {
    /** Returns the catalog server, or null when the ID does not exist. */
    McpServer getMcpById(String mcpId);

    /** Returns the user with their persisted toolkit, or null when absent. */
    User getUserById(String userId);

    /**
     * Persists the existing user's toolkit. Returns only after saving succeeds;
     * throws on failure. Implementations must preserve other toolkit entries.
     */
    void saveUser(User user);
}
