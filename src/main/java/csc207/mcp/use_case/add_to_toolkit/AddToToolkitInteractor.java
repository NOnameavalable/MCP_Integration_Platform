package csc207.mcp.use_case.add_to_toolkit;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.Mcpkit;
import csc207.mcp.entity.User;
import java.util.Objects;

/** Loads the selected MCP and user, updates their toolkit, and saves the result. */
public final class AddToToolkitInteractor implements AddToToolkitInputBoundary {
    private final AddToToolkitDataAccessInterface dataAccess;
    private final AddToToolkitOutputBoundary presenter;

    public AddToToolkitInteractor(AddToToolkitDataAccessInterface dataAccess,
                                  AddToToolkitOutputBoundary presenter) {
        this.dataAccess = Objects.requireNonNull(dataAccess);
        this.presenter = Objects.requireNonNull(presenter);
    }

    @Override
    public void excute(AddToToolkitInputData data) {
        if (data == null || data.getUserId() == null || data.getUserId().isBlank()) {
            presenter.prepareFailView("Please log in before adding to your toolkit.");
            return;
        }
        if (data.getServerId() == null || data.getServerId().isBlank()) {
            presenter.prepareFailView("Please select an MCP server.");
            return;
        }

        McpServer server;
        User user;
        try {
            server = dataAccess.getMcpById(data.getServerId());
            user = dataAccess.getUserById(data.getUserId());
        } catch (RuntimeException failure) {
            presenter.prepareFailView("Unable to load the MCP server or user. Please try again.");
            return;
        }
        if (server == null) {
            presenter.prepareFailView("MCP server not found.");
            return;
        }
        if (user == null) {
            presenter.prepareFailView("User not found.");
            return;
        }

        // Work on a copy so a failed save cannot change the loaded user's toolkit.
        Mcpkit updatedKit = new Mcpkit(user.getUserId());
        for (McpServer existing : user.getMcpkit().mcpkitGetter()) {
            updatedKit.addTool(existing);
        }
        if (!updatedKit.addTool(server)) {
            presenter.prepareFailView("This MCP server is already in your toolkit.");
            return;
        }
        User updatedUser = new User(user.getUserId(), user.getUsername(), user.getEmail(),
                user.getPasswordHash(), updatedKit);
        try {
            dataAccess.saveUser(updatedUser);
        } catch (RuntimeException failure) {
            presenter.prepareFailView("Unable to add the MCP server to your toolkit. Please try again.");
            return;
        }

        presenter.prepareSuccessView(new AddToToolkitOutputData(
                user.getUserId(), server.getMcpId(), server.getMcpName(), updatedKit.mcpkitGetter()));
    }
}
