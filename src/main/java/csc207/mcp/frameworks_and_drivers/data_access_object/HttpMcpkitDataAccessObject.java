package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.Tool;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitDataAccessInterface;
import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitDataAccessInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

/** Client-side HTTP DAO for user mcpkits and MCP server lookups. */
public final class HttpMcpkitDataAccessObject
        implements ListMcpkitDataAccessInterface, RemoveFromMcpkitDataAccessInterface {
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
