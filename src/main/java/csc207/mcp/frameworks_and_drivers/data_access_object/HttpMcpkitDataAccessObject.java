package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.Mcpkit;
import csc207.mcp.entity.Tool;
import csc207.mcp.entity.User;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitDataAccessInterface;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitDataAccessInterface;
import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitDataAccessInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

/** Client-side HTTP DAO for user mcpkits and MCP server lookups. */
public final class HttpMcpkitDataAccessObject
        implements ListMcpkitDataAccessInterface, RemoveFromMcpkitDataAccessInterface,
        AddToToolkitDataAccessInterface {
    private final HttpDbClient client;

    public HttpMcpkitDataAccessObject(HttpDbClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    @Override
    public boolean userExists(String userId) {
        JSONObject req = new JSONObject().put("userId", userId);
        JSONObject res = client.post("/db/mcpkit/user-exists", req);
        return res.getBoolean("exists");
    }

    @Override
    public McpServer getMcpById(String mcpId) {
        JSONObject req = new JSONObject().put("mcpId", mcpId);
        JSONObject res = client.post("/db/mcpkit/get-mcp-by-id", req);
        if (res.isNull("mcpServer")) {
            return null;
        }
        return fromJson(res.getJSONObject("mcpServer"));
    }

    @Override
    public User getUserById(String userId) {
        JSONObject res = client.post("/db/mcpkit/get-user-by-id", new JSONObject().put("userId", userId));
        if (res.isNull("user")) {
            return null;
        }
        JSONObject obj = res.getJSONObject("user");
        Mcpkit kit = new Mcpkit(obj.getString("userId"));
        JSONArray servers = obj.getJSONArray("servers");
        for (int i = 0; i < servers.length(); i++) {
            kit.addTool(fromJson(servers.getJSONObject(i)));
        }
        return new User(obj.getString("userId"), obj.getString("username"), obj.getString("email"),
                obj.getString("passwordHash"), kit);
    }

    /** Sends user and MCP identifiers to the server, which persists the toolkit. */
    @Override
    public void saveUser(User user) {
        Objects.requireNonNull(user, "user must not be null");
        if (!user.getUserId().equals(user.getMcpkit().userIdGetter())) {
            throw new IllegalArgumentException("Toolkit must belong to the user being saved");
        }
        JSONArray serverIds = new JSONArray();
        for (McpServer server : user.getMcpkit().mcpkitGetter()) {
            serverIds.put(server.getMcpId());
        }
        JSONObject req = new JSONObject().put("userId", user.getUserId()).put("serverIds", serverIds);
        JSONObject res = client.post("/db/mcpkit/save-user", req);
        if (!res.getBoolean("ok")) {
            throw new IllegalStateException("Server did not save user's toolkit");
        }
    }

    @Override
    public List<McpServer> getMcpkitByUserId(String userId) {
        JSONObject req = new JSONObject().put("userId", userId);
        JSONObject res = client.post("/db/mcpkit/get-by-user-id", req);
        JSONArray array = res.getJSONArray("servers");
        List<McpServer> servers = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            servers.add(fromJson(array.getJSONObject(i)));
        }
        return servers;
    }

    @Override
    public void removeFromUserMcpkit(String userId, String mcpId) {
        JSONObject req = new JSONObject()
                .put("userId", userId)
                .put("mcpId", mcpId);
        client.post("/db/mcpkit/remove", req);
    }

    private static McpServer fromJson(JSONObject obj) {
        McpServer server = new McpServer(
                obj.getString("name"),
                obj.getString("description"),
                obj.getString("mcpId"));
        JSONArray toolsArray = obj.getJSONArray("tools");
        for (int i = 0; i < toolsArray.length(); i++) {
            JSONObject toolObj = toolsArray.getJSONObject(i);
            JSONArray paramsArray = toolObj.getJSONArray("parameters");
            List<String> params = new ArrayList<>(paramsArray.length());
            for (int j = 0; j < paramsArray.length(); j++) {
                params.add(paramsArray.getString(j));
            }
            server.addTool(new Tool(
                    toolObj.getString("name"),
                    toolObj.getString("description"),
                    params));
        }
        return server;
    }
}
