package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.McpServer;
import csc207.mcp.use_case.invoke.InvokeDataAccessInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

/** SQLite DAO for gateway invocation authentication and tool authorization. */
public class SqliteInvokeDataAccessObject implements InvokeDataAccessInterface {
    private final SqliteDatabaseManager dbManager;
    private final SqliteMcpkitDataAccessObject mcpkitDao;

    public SqliteInvokeDataAccessObject(SqliteDatabaseManager dbManager,
            SqliteMcpkitDataAccessObject mcpkitDao) {
        this.dbManager = dbManager;
        this.mcpkitDao = mcpkitDao;
    }

    @Override
    public ApiKey findApiKeyByHash(String keyHash) {
        String sql = "SELECT key_id, owner_user_id, key_hash, display_prefix, "
                + "created_at, last_used_at, revoked_at FROM api_keys WHERE key_hash = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String lastUsedStr = rs.getString("last_used_at");
                    String revokedStr = rs.getString("revoked_at");
                    return new ApiKey(
                            rs.getString("key_id"),
                            rs.getString("owner_user_id"),
                            rs.getString("key_hash"),
                            rs.getString("display_prefix"),
                            Instant.parse(rs.getString("created_at")),
                            lastUsedStr != null ? Instant.parse(lastUsedStr) : null,
                            revokedStr != null ? Instant.parse(revokedStr) : null);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find API key by hash", e);
        }
    }

    @Override
    public List<McpServer> getMcpkitByUserId(String userId) {
        String sql = "SELECT mcp_id FROM mcp_kits WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<McpServer> servers = new ArrayList<>();
                while (rs.next()) {
                    servers.add(mcpkitDao.getMcpById(rs.getString("mcp_id")));
                }
                return servers;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to get MCPkits by user ID", e);
        }

    }
}
