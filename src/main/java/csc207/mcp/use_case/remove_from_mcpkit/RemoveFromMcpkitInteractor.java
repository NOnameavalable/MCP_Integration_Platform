package csc207.mcp.use_case.remove_from_mcpkit;

import csc207.mcp.entity.McpServer;
import java.util.List;

public class RemoveFromMcpkitInteractor implements RemoveFromMcpkitInputBoundary {
    private final RemoveFromMcpkitDataAccessInterface dataAccess;
    private final RemoveFromMcpkitOutputBoundary presenter;

    public RemoveFromMcpkitInteractor(RemoveFromMcpkitDataAccessInterface dataAccess,
            RemoveFromMcpkitOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void removeFromMcpkit(RemoveFromMcpkitInputData inputData) {
        String userId = inputData.getUserId();
        String mcpId = inputData.getMcpId();
        boolean userExists = dataAccess.userExists(userId);
        McpServer mcp = dataAccess.getMcpById(mcpId);

        if (!userExists || mcp == null) {
            List<McpServer> currentList = userExists ? dataAccess.getMcpkitByUserId(userId) : List.of();
            RemoveFromMcpkitOutputData outputData = new RemoveFromMcpkitOutputData(
                    false, null, null, currentList, "user not found or mcp not found");
            presenter.prepareView(outputData);
            return;
        }

        List<McpServer> mcpkitList = dataAccess.getMcpkitByUserId(userId);
        if (mcpkitList == null || !mcpkitList.contains(mcp)) {
            RemoveFromMcpkitOutputData outputData = new RemoveFromMcpkitOutputData(
                    false, mcp.getMcpId(), mcp.getMcpName(),
                    mcpkitList == null ? List.of() : mcpkitList,
                    "mcp not in user mcpkit");
            presenter.prepareView(outputData);
            return;
        }

        dataAccess.removeFromUserMcpkit(userId, mcpId);
        List<McpServer> updatedMcpkit = dataAccess.getMcpkitByUserId(userId);
        RemoveFromMcpkitOutputData outputData = new RemoveFromMcpkitOutputData(
                true, mcp.getMcpId(), mcp.getMcpName(),
                updatedMcpkit == null ? List.of() : updatedMcpkit,
                "mcp has been removed");
        presenter.prepareView(outputData);
    }
}
