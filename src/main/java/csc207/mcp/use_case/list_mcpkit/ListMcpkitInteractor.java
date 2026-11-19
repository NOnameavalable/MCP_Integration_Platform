package csc207.mcp.use_case.list_mcpkit;

import csc207.mcp.entity.McpServer;

/**
 * Mcpkit-module scaffold. TODO: implement ListMcpkitInteractor.
 */
public class ListMcpkitInteractor implements ListMcpkitInputBoundary {
    private final ListMcpkitDataAccessInterface dataAccess;
    private final ListMcpkitOutputBoundary presenter;

    public ListMcpkitInteractor(ListMcpkitDataAccessInterface dataAccess,
            ListMcpkitOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void listMcpkit(String userId) {
        McpServer[] mcpkit = dataAccess.getMcpkitByUserId(userId);

    }
}
