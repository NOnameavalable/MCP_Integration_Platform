package csc207.mcp.use_case.invoke;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.McpServer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import csc207.mcp.entity.Tool;

public class InvokeInteractor implements InvokeInputBoundary {
    private final InvokeDataAccessInterface dataAccess;
    private final McpToolExecutor toolExecutor;

    public InvokeInteractor(InvokeDataAccessInterface dataAccess, McpToolExecutor toolExecutor) {
        this.dataAccess = dataAccess;
        this.toolExecutor = toolExecutor;
    }

    @Override
    public void execute(InvokeInputData inputData, InvokeOutputBoundary presenter) {
        String key = inputData.getKey();
        String request = inputData.getRequest();
        String keyHash = hashApiKey(key);
        ApiKey apikey = dataAccess.findApiKeyByHash(keyHash);

        if (apikey == null || !apikey.isActive()) {
            presenter.prepareFailView("401", "Invalid API Key");
            return;
        }
        String method;
        Object id;
        String toolName = null;
        String userId = apikey.getOwnerUserId();
        Map<String, Object> arguments = Map.of();

        try {
            JSONObject root = new JSONObject(request);
            method = root.getString("method");
            id = root.opt("id");
            if ("tools/call".equals(method)) {
                JSONObject params = root.getJSONObject("params");
                toolName = params.getString("name");
                JSONObject argsJson = params.optJSONObject("arguments");
                if (argsJson != null) {
                    arguments = argsJson.toMap();
                }
            }
        } catch (JSONException e) {
            presenter.prepareFailView("400", "Invalid Request");
            return;
        }

        if ("initialize".equals(method)) {
            JSONObject initializeResponse = initializeRequest(id);
            presenter.prepareSuccessView("200", initializeResponse.toString());
            return;
        }

        if ("tools/call".equals(method)) {
            if (!hasToolAccess(userId, toolName)) {
                presenter.prepareFailView("404", "Tool not found");
                return;
            }
            try {
                JSONObject toolCall = toolsCallRequest(toolName, arguments, id);
                presenter.prepareSuccessView("200", toolCall.toString());
            } catch (ToolExecutionException e) {
                presenter.prepareFailView(e.getStatusCode(), e.getMessage());
            } catch (Exception e) {
                presenter.prepareFailView("500", "Internal Server Error");
            }
            return;
        }

        if ("tools/list".equals(method)) {
            JSONObject toolList = toolListRequest(userId, id);
            presenter.prepareSuccessView("200", toolList.toString());
            return;
        }

        presenter.prepareFailView("400", "Unsupported method: " + method);
    }

    private String hashApiKey(String key) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(key.getBytes(StandardCharsets.UTF_8));
            return "sha256$" + Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private boolean hasToolAccess(String userId, String toolName) {
        List<McpServer> mcpServers = dataAccess.getMcpkitByUserId(userId);
        if (mcpServers == null) {
            return false;
        }
        for (McpServer mcpServer : mcpServers) {
            for (Tool tool : mcpServer.getTools()) {
                if (toolName.equals(tool.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private JSONObject toolsCallRequest(String toolName, Map<String, Object> arguments, Object id) {
        String toolOutput = toolExecutor.execute(toolName, arguments);

        JSONObject textItem = new JSONObject();
        textItem.put("type", "text");
        textItem.put("text", toolOutput);

        JSONArray content = new JSONArray();
        content.put(textItem);

        JSONObject result = new JSONObject();
        result.put("content", content);

        JSONObject response = new JSONObject();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result", result);
        return response;
    }

    private JSONObject toolListRequest(String userId, Object id) {
        List<McpServer> mcpServers = dataAccess.getMcpkitByUserId(userId);
        JSONArray toolsArray = new JSONArray();

        if (mcpServers != null) {
            for (McpServer mcpServer : mcpServers) {
                for (Tool tool : mcpServer.getTools()) {
                    JSONObject toolJson = new JSONObject();
                    toolJson.put("name", tool.getName());
                    toolJson.put("description", tool.getDescription());
                    toolsArray.put(toolJson);
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("tools", toolsArray);

        JSONObject response = new JSONObject();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result", result);
        return response;
    }

    private JSONObject initializeRequest(Object id) {
        JSONObject serverInfo = new JSONObject();
        serverInfo.put("name", "mcp-integration-platform");
        serverInfo.put("version", "1.0.0");

        JSONObject capabilities = new JSONObject();
        capabilities.put("tools", new JSONObject());

        JSONObject result = new JSONObject();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", capabilities);
        result.put("serverInfo", serverInfo);

        JSONObject response = new JSONObject();
        response.put("jsonrpc", "2.0");
        response.put("id", id);
        response.put("result", result);
        return response;
    }
}
