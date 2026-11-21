package csc207.mcp.frameworks_and_drivers.data_access_object;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;

public class SqliteDatabaseManager {
    private final String dbUrl;

    public SqliteDatabaseManager(String dbFilePath) {
        this.dbUrl = "dbUrl:sqllite:" + dbFilePath;
        ensureParentDirectoryExists(dbFilePath);
    }

    public void ensureParentDirectoryExists(String dbFilePath) {
        Path dbPath = Path.of(dbFilePath);
        Path parentDir = dbPath.getParent();
        if (parentDir != null) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create parent directory: " + parentDir, e);
            }
        }
    }

    public void initializeTables() {
        String createTableSql = """
                    CREATE TABLE IF NOT EXISTS users (
                        user_id TEXT PRIMARY KEY,
                        username TEXT NOT NULL UNIQUE,
                        email TEXT NOT NULL,
                        password_hash TEXT NOT NULL
                    )
                """;

        String createApiKeysTable = """
                    CREATE TABLE IF NOT EXISTS api_keys (
                        key_id TEXT PRIMARY KEY,
                        owner_user_id TEXT NOT NULL,
                        key_hash TEXT NOT NULL UNIQUE,
                        display_prefix TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        last_used_at TEXT,
                        revoked_at TEXT,
                        FOREIGN KEY (owner_user_id) REFERENCES users(user_id) ON DELETE CASCADE
                    );
                """;

        String createMcpServersTable = """
                    CREATE TABLE IF NOT EXISTS mcp_servers (
                        mcp_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT NOT NULL
                    );
                """;

        String createToolsTable = """
                    CREATE TABLE IF NOT EXISTS tools (
                        mcp_id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        parameters_json TEXT NOT NULL,
                        PRIMARY KEY (mcp_id, name),
                        FOREIGN KEY (mcp_id) REFERENCES mcp_servers(mcp_id) ON DELETE CASCADE
                );
                """;

        String createUserMcpkitTable = """
                    CREATE TABLE IF NOT EXISTS user_mcpkit (
                        user_id TEXT NOT NULL,
                        mcp_id TEXT NOT NULL,
                        PRIMARY KEY (user_id, mcp_id),
                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                        FOREIGN KEY (mcp_id) REFERENCES mcp_servers(mcp_id) ON DELETE CASCADE
                    );
                """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            stmt.execute(createApiKeysTable);
            stmt.execute(createMcpServersTable);
            stmt.execute(createToolsTable);
            stmt.execute(createUserMcpkitTable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize tables", e);
        }
    }

}