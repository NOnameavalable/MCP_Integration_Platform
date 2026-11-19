package csc207.mcp.use_case.list_mcpkit;

import csc207.mcp.entity.McpServer;
import java.util.List;

public class ListMcpkitInteractor implements ListMcpkitInputBoundary {
    private final ListMcpkitDataAccessInterface dataAccess;
    private final ListMcpkitOutputBoundary presenter;

    public ListMcpkitInteractor(ListMcpkitDataAccessInterface dataAccess,
            ListMcpkitOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void listMcpkit(ListMcpkitInputData inputData) {
        if (inputData == null || inputData.getUserId() == null || inputData.getUserId().isBlank()) {
            presenter.prepareView(new ListMcpkitOutputData(List.of(), false, "Invalid user ID"));
            return;
        }

        String userId = inputData.getUserId();
        McpServer[] mcpkit = dataAccess.getMcpkitByUserId(userId);

        if (mcpkit == null) {
            presenter.prepareView(new ListMcpkitOutputData(List.of(), false, "Failed to load mcpkit"));
        } else if (mcpkit.length == 0) {
            presenter.prepareView(new ListMcpkitOutputData(List.of(), true, "Mcpkit is empty"));
        } else {
            presenter.prepareView(new ListMcpkitOutputData(List.of(mcpkit), true, "List mcpkit successful"));
        }
    }
}
