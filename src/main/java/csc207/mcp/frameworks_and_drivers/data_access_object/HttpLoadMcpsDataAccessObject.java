package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.Mcp;
import csc207.mcp.entity.Tool;
import csc207.mcp.use_case.load_mcps.LoadMcpsDataAccessInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

/** Client-side HTTP DAO for loading all marketplace MCP servers. */
public final class HttpLoadMcpsDataAccessObject implements LoadMcpsDataAccessInterface {
    private final HttpDbClient client;

    public HttpLoadMcpsDataAccessObject(HttpDbClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    @Override
    public List<Mcp> loadAllMcps() {
        JSONObject res = client.post("/db/mcps/load-all", new JSONObject());
        JSONArray array = res.getJSONArray("mcps");
        List<Mcp> mcps = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            mcps.add(fromJson(array.getJSONObject(i)));
        }
        return mcps;
    }

    private static Mcp fromJson(JSONObject obj) {
        JSONArray toolsArray = obj.getJSONArray("tools");
        List<Tool> tools = new ArrayList<>(toolsArray.length());
        for (int i = 0; i < toolsArray.length(); i++) {
            JSONObject toolObj = toolsArray.getJSONObject(i);
            JSONArray paramsArray = toolObj.getJSONArray("parameters");
            List<String> params = new ArrayList<>(paramsArray.length());
            for (int j = 0; j < paramsArray.length(); j++) {
                params.add(paramsArray.getString(j));
            }
            tools.add(new Tool(
                    toolObj.getString("name"),
                    toolObj.getString("description"),
                    params));
        }
        return new Mcp(
                obj.getString("name"),
                obj.getString("description"),
                obj.getString("mcpId"),
                tools);
    }
}
