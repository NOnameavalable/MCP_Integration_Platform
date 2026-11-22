package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.User;
import csc207.mcp.use_case.login.LoginDataAccessInterface;
import csc207.mcp.use_case.signup.SignupDataAccessInterface;

/** SQLite DAO for user registration and authentication. */
public class SqliteUserDataAccessObject
        implements SignupDataAccessInterface, LoginDataAccessInterface {
    private final SqliteDatabaseManager dbManager;

    public SqliteUserDataAccessObject(SqliteDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public boolean existsByUsername(String username) {
        // TODO: SELECT 1 FROM users WHERE username = ?
        // SQLException -> throw new IllegalStateException(...)
        throw new UnsupportedOperationException("TODO: implement existsByUsername");
    }

    @Override
    public void save(User user) {
        // TODO: INSERT INTO users (user_id, username, email, password_hash) VALUES (?, ?, ?, ?)
        // UNIQUE/PK conflict (errorCode == 19) -> throw new IllegalArgumentException(...)
        // Other SQLException -> throw new IllegalStateException(...)
        throw new UnsupportedOperationException("TODO: implement save");
    }

    @Override
    public User findByIdentifier(String identifier) {
        // TODO: SELECT user_id, username, email, password_hash FROM users WHERE username = ? OR email = ?
        // Return null if not found
        throw new UnsupportedOperationException("TODO: implement findByIdentifier");
    }
}
