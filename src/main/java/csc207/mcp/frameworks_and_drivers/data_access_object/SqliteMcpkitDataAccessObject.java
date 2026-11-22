package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.McpServer;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitDataAccessInterface;
import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitDataAccessInterface;
import java.util.List;

/** SQLite DAO for user mcpkits and MCP server catalog lookups. */
public class SqliteMcpkitDataAccessObject
        implements ListMcpkitDataAccessInterface, RemoveFromMcpkitDataAccessInterface {
    private final SqliteDatabaseManager dbManager;

    public SqliteMcpkitDataAccessObject(SqliteDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean userExists(String userId) {
        // TODO: SELECT 1 FROM users WHERE user_id = ?
        throw new UnsupportedOperationException("TODO: implement userExists");
    }

    @Override
    public McpServer getMcpById(String mcpId) {
        // TODO: SELECT mcp_id, name, description FROM mcp_servers WHERE mcp_id = ?
        // Also load its tools from tools table and call server.addTool(...)
        // Return null if not found
        throw new UnsupportedOperationException("TODO: implement getMcpById");
    }

    @Override
    public List<McpServer> getMcpkitByUserId(String userId) {
        // TODO: SELECT s.mcp_id, s.name, s.description
        //       FROM user_mcpkit um JOIN mcp_servers s ON um.mcp_id = s.mcp_id
        //       WHERE um.user_id = ?
        // Load tools for each McpServer and return the list
        throw new UnsupportedOperationException("TODO: implement getMcpkitByUserId");
    }

    @Override
    public void removeFromUserMcpkit(String userId, String mcpId) {
        // TODO: DELETE FROM user_mcpkit WHERE user_id = ? AND mcp_id = ?
        throw new UnsupportedOperationException("TODO: implement removeFromUserMcpkit");
    }
}
