package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.Tool;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitDataAccessInterface;
import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitDataAccessInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/** SQLite DAO for user mcpkits and MCP server catalog lookups. */
public class SqliteMcpkitDataAccessObject
        implements ListMcpkitDataAccessInterface, RemoveFromMcpkitDataAccessInterface {
    private final SqliteDatabaseManager dbManager;

    public SqliteMcpkitDataAccessObject(SqliteDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean userExists(String userId) {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if user exists", e);
        }
    }

    @Override
    public McpServer getMcpById(String mcpId) {
        String sql = "SELECT mcp_id, name, description FROM mcp_servers WHERE mcp_id = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mcpId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    McpServer server = new McpServer(
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("mcp_id"));
                    loadToolsForServer(conn, server);
                    return server;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to get MCP by ID", e);
        }
    }

    private void loadToolsForServer(Connection conn, McpServer server) throws SQLException {
        String sql = "SELECT name, description, parameters_json FROM tools WHERE mcp_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, server.getMcpId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JSONArray paramsJson = new JSONArray(rs.getString("parameters_json"));
                    List<String> parameters = new ArrayList<>();
                    for (int i = 0; i < paramsJson.length(); i++) {
                        parameters.add(paramsJson.getString(i));
                    }
                    server.addTool(new Tool(
                            rs.getString("name"),
                            rs.getString("description"),
                            parameters));
                }
            }
        }
    }

    @Override
    public List<McpServer> getMcpkitByUserId(String userId) {
        String sql = "SELECT m.mcp_id, m.name, m.description "
                + "FROM user_mcpkit um "
                + "JOIN mcp_servers m ON um.mcp_id = m.mcp_id "
                + "WHERE um.user_id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<McpServer> servers = new ArrayList<>();
                while (rs.next()) {
                    McpServer server = new McpServer(
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("mcp_id"));
                    loadToolsForServer(conn, server);
                    servers.add(server);
                }
                return servers;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to get MCPkits by user ID", e);
        }
    }

    @Override
    public void removeFromUserMcpkit(String userId, String mcpId) {
        String sql = "DELETE FROM user_mcpkit WHERE user_id = ? AND mcp_id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, mcpId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove MCP from user's MCPkit", e);
        }
    }
}
