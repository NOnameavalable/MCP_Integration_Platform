package csc207.mcp.frameworks_and_drivers.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.Tool;
import csc207.mcp.entity.User;
import csc207.mcp.frameworks_and_drivers.data_access_object.SqliteMcpkitDataAccessObject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

public final class McpkitDbHttpHandler implements HttpHandler {
    private final SqliteMcpkitDataAccessObject mcpkitDao;
    private final String internalSecret;

    public McpkitDbHttpHandler(SqliteMcpkitDataAccessObject mcpkitDao, String internalSecret) {
        this.mcpkitDao = Objects.requireNonNull(mcpkitDao, "mcpkitDao must not be null");
        this.internalSecret = DbHttpSupport.requireSecret(internalSecret);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DbHttpSupport.dispatch(exchange, internalSecret, (path, req) -> {
            switch (path) {
                case "/db/mcpkit/user-exists" -> {
                    boolean exists = mcpkitDao.userExists(req.getString("userId"));
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("exists", exists));
                }
                case "/db/mcpkit/get-mcp-by-id" -> {
                    McpServer server = mcpkitDao.getMcpById(req.getString("mcpId"));
                    JSONObject res = new JSONObject();
                    if (server == null) {
                        res.put("mcpServer", JSONObject.NULL);
                    } else {
                        res.put("mcpServer", toJson(server));
                    }
                    DbHttpSupport.sendJson(exchange, 200, res);
                }
                case "/db/mcpkit/get-user-by-id" -> {
                    User user = mcpkitDao.getUserById(req.getString("userId"));
                    JSONObject res = new JSONObject().put("user", user == null ? JSONObject.NULL : toJson(user));
                    DbHttpSupport.sendJson(exchange, 200, res);
                }
                case "/db/mcpkit/save-user" -> {
                    User user = mcpkitDao.getUserById(req.getString("userId"));
                    if (user == null) {
                        throw new IllegalArgumentException("User not found");
                    }
                    JSONArray serverIds = req.getJSONArray("serverIds");
                    for (int i = 0; i < serverIds.length(); i++) {
                        McpServer server = mcpkitDao.getMcpById(serverIds.getString(i));
                        if (server == null) {
                            throw new IllegalArgumentException("MCP server not found");
                        }
                        user.getMcpkit().addTool(server);
                    }
                    mcpkitDao.saveUser(user);
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("ok", true));
                }
                case "/db/mcpkit/get-by-user-id" -> {
                    List<McpServer> servers = mcpkitDao.getMcpkitByUserId(req.getString("userId"));
                    JSONArray array = new JSONArray();
                    for (McpServer server : servers) {
                        array.put(toJson(server));
                    }
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("servers", array));
                }
                case "/db/mcpkit/remove" -> {
                    mcpkitDao.removeFromUserMcpkit(req.getString("userId"), req.getString("mcpId"));
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("ok", true));
                }
                default -> DbHttpSupport.sendError(exchange, 404, "Unknown endpoint: " + path);
            }
        });
    }

    private static JSONObject toJson(User user) {
        JSONArray servers = new JSONArray();
        for (McpServer server : user.getMcpkit().mcpkitGetter()) {
            servers.put(toJson(server));
        }
        return new JSONObject()
                .put("userId", user.getUserId())
                .put("username", user.getUsername())
                .put("email", user.getEmail())
                .put("passwordHash", user.getPasswordHash())
                .put("servers", servers);
    }

    private static JSONObject toJson(McpServer server) {
        JSONObject obj = new JSONObject();
        obj.put("mcpId", server.getMcpId());
        obj.put("name", server.getMcpName());
        obj.put("description", server.getDescription());
        JSONArray toolsArray = new JSONArray();
        for (Tool tool : server.getTools()) {
            JSONObject toolObj = new JSONObject();
            toolObj.put("name", tool.getName());
            toolObj.put("description", tool.getDescription());
            toolObj.put("parameters", new JSONArray(tool.getParameters()));
            toolsArray.put(toolObj);
        }
        obj.put("tools", toolsArray);
        return obj;
    }
}
