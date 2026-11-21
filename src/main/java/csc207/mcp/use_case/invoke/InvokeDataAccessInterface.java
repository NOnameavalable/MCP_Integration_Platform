package csc207.mcp.use_case.invoke;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.Mcpkit;

public interface InvokeDataAccessInterface {
    ApiKey findApiKeyByHash(String keyHash);

    Mcpkit findMcpKitByUserId(String userId);
}
