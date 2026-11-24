package csc207.mcp.frameworks_and_drivers.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import csc207.mcp.entity.Mcp;
import csc207.mcp.entity.Tool;
import csc207.mcp.frameworks_and_drivers.data_access_object.SqliteLoadMcpsDataAccessObject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

public final class LoadMcpsDbHttpHandler implements HttpHandler {
    private final SqliteLoadMcpsDataAccessObject loadMcpsDao;
    private final String internalSecret;

    public LoadMcpsDbHttpHandler(SqliteLoadMcpsDataAccessObject loadMcpsDao, String internalSecret) {
        this.loadMcpsDao = Objects.requireNonNull(loadMcpsDao, "loadMcpsDao must not be null");
        this.internalSecret = DbHttpSupport.requireSecret(internalSecret);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DbHttpSupport.dispatch(exchange, internalSecret, (path, req) -> {
            if (!"/db/mcps/load-all".equals(path)) {
                DbHttpSupport.sendError(exchange, 404, "Unknown endpoint: " + path);
                return;
            }
            List<Mcp> mcps = loadMcpsDao.loadAllMcps();
            JSONArray array = new JSONArray();
            for (Mcp mcp : mcps) {
                array.put(toJson(mcp));
            }
            DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("mcps", array));
        });
    }

    private static JSONObject toJson(Mcp mcp) {
        JSONObject obj = new JSONObject();
        obj.put("mcpId", mcp.getMcpId());
        obj.put("name", mcp.getMcpName());
        obj.put("description", mcp.getDescription());
        JSONArray toolsArray = new JSONArray();
        for (Tool tool : mcp.getTools()) {
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
