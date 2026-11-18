package csc207.mcp.use_case.remove_from_mcpkit;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.User;

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
        User user = dataAccess.getUserById(inputData.getUserId());
        McpServer mcp = dataAccess.getMcpById(inputData.getMcpId());
        if (user == null || mcp == null) {
            RemoveFromMcpkitOutputData outputData = new RemoveFromMcpkitOutputData(false, null, null,
                    "user not found or mcp not found");
            presenter.prepareView(outputData);
            return;
        }
        if (!user.getMcpkit().contains(mcp)) {
            RemoveFromMcpkitOutputData outputData = new RemoveFromMcpkitOutputData(false, mcp.getMcpId(),
                    mcp.getMcpName(), "mcp not in user mcpkit");
            presenter.prepareView(outputData);
            return;
        } else {
            user.getMcpkit().removeTool(mcp);
            dataAccess.removeFromUserMcpkit(user.getUserId(), inputData.getMcpId());
            RemoveFromMcpkitOutputData outputData = new RemoveFromMcpkitOutputData(true, mcp.getMcpId(),
                    mcp.getMcpName(), "mcp has been removed");
            presenter.prepareView(outputData);
        }
    }
}
