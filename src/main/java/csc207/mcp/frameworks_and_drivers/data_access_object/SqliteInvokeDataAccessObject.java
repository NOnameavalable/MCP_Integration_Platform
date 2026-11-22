package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.McpServer;
import csc207.mcp.use_case.invoke.InvokeDataAccessInterface;
import java.util.List;

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
        // TODO: SELECT ... FROM api_keys WHERE key_hash = ?
        // Return null if not found
        throw new UnsupportedOperationException("TODO: implement findApiKeyByHash");
    }

    @Override
    public List<McpServer> getMcpkitByUserId(String userId) {
        // TODO: delegate to mcpkitDao.getMcpkitByUserId(userId)
        throw new UnsupportedOperationException("TODO: implement getMcpkitByUserId");
    }
}
