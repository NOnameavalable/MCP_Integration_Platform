package csc207.mcp.interface_adapter.remove_from_mcpkit;

import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitInputBoundary;
import csc207.mcp.use_case.remove_from_mcpkit.RemoveFromMcpkitInputData;

public class RemoveFromMcpkitController {
    private final RemoveFromMcpkitInputBoundary removeFromMcpkitInteractor;

    public RemoveFromMcpkitController(RemoveFromMcpkitInputBoundary removeFromMcpkitInteractor) {
        this.removeFromMcpkitInteractor = removeFromMcpkitInteractor;
    }

    public void execute(String userId, String mcpId) {
        RemoveFromMcpkitInputData inputData = new RemoveFromMcpkitInputData(userId, mcpId);
        removeFromMcpkitInteractor.removeFromMcpkit(inputData);
    }
}
