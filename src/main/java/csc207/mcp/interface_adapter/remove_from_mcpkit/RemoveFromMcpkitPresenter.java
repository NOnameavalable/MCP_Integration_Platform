package csc207.mcp.interface_adapter.remove_from_mcpkit;

import csc207.mcp.interface_adapter.mcpkit.McpkitState;
import csc207.mcp.interface_adapter.mcpkit.McpkitViewModel;
import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitOutputBoundary;
import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitOutputData;

public class RemoveFromMcpkitPresenter implements RemoveFromMcpkitOutputBoundary {
    private final McpkitViewModel mcpkitViewModel;

    public RemoveFromMcpkitPresenter(McpkitViewModel mcpkitViewModel) {
        this.mcpkitViewModel = mcpkitViewModel;
    }

    @Override
    public void prepareView(RemoveFromMcpkitOutputData outputData) {
        String statusText = outputData.isSuccess() ? "success" : "failed";
        String message = statusText + ", mcp:" + outputData.getMcpName() + ", mcpid:"
                + outputData.getMcpId() + "," + outputData.getMessage();
        mcpkitViewModel.setState(new McpkitState(message, outputData.isSuccess()));
    }
}
