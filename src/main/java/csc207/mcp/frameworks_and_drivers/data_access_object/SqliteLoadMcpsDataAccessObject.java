package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.Mcp;
import csc207.mcp.entity.Tool;
import csc207.mcp.use_case.load_mcps.LoadMcpsDataAccessInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;

/** SQLite DAO for loading all marketplace MCP servers and their tools. */
public final class SqliteLoadMcpsDataAccessObject implements LoadMcpsDataAccessInterface {
    private final SqliteDatabaseManager dbManager;

    public SqliteLoadMcpsDataAccessObject(SqliteDatabaseManager dbManager) {
        this.dbManager = Objects.requireNonNull(dbManager, "dbManager must not be null");
    }

    @Override
    public List<Mcp> loadAllMcps() {
        String sql = "SELECT mcp_id, name, description FROM mcp_servers ORDER BY name, mcp_id";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Mcp> mcps = new ArrayList<>();
            while (rs.next()) {
                String mcpId = rs.getString("mcp_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                List<Tool> tools = loadToolsForMcp(conn, mcpId);
                mcps.add(new Mcp(name, description, mcpId, tools));
            }
            return mcps;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load MCP catalog", e);
        }
    }

    private List<Tool> loadToolsForMcp(Connection conn, String mcpId) throws SQLException {
        String sql = "SELECT name, description, parameters_json FROM tools "
                + "WHERE mcp_id = ? ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mcpId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Tool> tools = new ArrayList<>();
                while (rs.next()) {
                    JSONArray paramsJson = new JSONArray(rs.getString("parameters_json"));
                    List<String> parameters = new ArrayList<>();
                    for (int i = 0; i < paramsJson.length(); i++) {
                        parameters.add(paramsJson.getString(i));
                    }
                    tools.add(new Tool(
                            rs.getString("name"),
                            rs.getString("description"),
                            parameters));
                }
                return tools;
            }
        }
    }
}
