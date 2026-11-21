package csc207.mcp.use_case.invoke;

import java.util.Map;

public interface McpToolExecutor {
    String execute(String toolName, Map<String, Object> arguments) throws ToolExecutionException;
}
