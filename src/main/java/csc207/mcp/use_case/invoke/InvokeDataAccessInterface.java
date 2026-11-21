package csc207.mcp.use_case.invoke;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.McpServer;
import java.util.List;

public interface InvokeDataAccessInterface {
    ApiKey findApiKeyByHash(String keyHash);

    List<McpServer> getMcpkitByUserId(String userId);
}
