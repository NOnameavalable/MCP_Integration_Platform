package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.use_case.create_api_key.CreateApiKeyDataAccessInterface;
import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyDataAccessInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

/** SQLite DAO for creating, querying, and revoking API keys. */
public class SqliteApiKeyDataAccessObject
        implements CreateApiKeyDataAccessInterface, RevokeApiKeyDataAccessInterface {
    private final SqliteDatabaseManager dbManager;

    public SqliteApiKeyDataAccessObject(SqliteDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void save(ApiKey apiKey) {
        String sql = "INSERT INTO api_keys "
                + "(key_id, owner_user_id, key_hash, display_prefix, created_at, last_used_at, revoked_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, apiKey.getKeyId());
            ps.setString(2, apiKey.getOwnerUserId());
            ps.setString(3, apiKey.getKeyHash());
            ps.setString(4, apiKey.getDisplayPrefix());
            ps.setString(5, apiKey.getCreatedAt().toString());
            ps.setString(6, apiKey.getLastUsedAt() != null ? apiKey.getLastUsedAt().toString() : null);
            ps.setString(7, apiKey.getRevokedAt() != null ? apiKey.getRevokedAt().toString() : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new IllegalArgumentException("API key conflicts with an existing record", e);
            }
            throw new IllegalStateException("Failed to save API key", e);
        }
    }

    @Override
    public Optional<ApiKey> findById(String keyId) {
        String sql = "SELECT key_id, owner_user_id, key_hash, display_prefix, "
                + "created_at, last_used_at, revoked_at FROM api_keys WHERE key_id = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String lastUsedStr = rs.getString("last_used_at");
                    String revokedStr = rs.getString("revoked_at");
                    ApiKey apiKey = new ApiKey(
                            rs.getString("key_id"),
                            rs.getString("owner_user_id"),
                            rs.getString("key_hash"),
                            rs.getString("display_prefix"),
                            Instant.parse(rs.getString("created_at")),
                            lastUsedStr != null ? Instant.parse(lastUsedStr) : null,
                            revokedStr != null ? Instant.parse(revokedStr) : null);
                    return Optional.of(apiKey);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load API key", e);
        }
    }

    @Override
    public void revoke(String keyId, String ownerUserId) {
        String sql = "UPDATE api_keys SET revoked_at = COALESCE(revoked_at, ?) WHERE key_id = ? AND owner_user_id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Instant.now().toString());
            ps.setString(2, keyId);
            ps.setString(3, ownerUserId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("API key not found or access denied");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to revoke API key", e);
        }
    }
}
