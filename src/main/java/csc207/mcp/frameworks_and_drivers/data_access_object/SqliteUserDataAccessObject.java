package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.User;
import csc207.mcp.use_case.login.LoginDataAccessInterface;
import csc207.mcp.use_case.signup.SignupDataAccessInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** SQLite DAO for user registration and authentication. */
public class SqliteUserDataAccessObject
        implements SignupDataAccessInterface, LoginDataAccessInterface {
    private final SqliteDatabaseManager dbManager;

    public SqliteUserDataAccessObject(SqliteDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check if username exists", e);
        }
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (user_id, username, email, password_hash) VALUES (?, ?, ?, ?) ";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new UnsupportedOperationException("TODO: implement save");
        }
    }

    @Override
    public User findByIdentifier(String identifier) {
        String sql = "SELECT FROM users WHERE "
        throw new UnsupportedOperationException("TODO: implement findByIdentifier");
    }
}
