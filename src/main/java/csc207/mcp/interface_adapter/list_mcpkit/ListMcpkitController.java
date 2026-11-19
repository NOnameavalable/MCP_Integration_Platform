package csc207.mcp.interface_adapter.list_mcpkit;

import csc207.mcp.entity.User;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitInputBoundary;

/**
 * Controller for the list-mcpkit use case.
 */
public class ListMcpkitController {
    private final ListMcpkitInputBoundary interactor;
    private final User user;

    public ListMcpkitController(ListMcpkitInputBoundary interactor, User user) {
        this.interactor = interactor;
        this.user = user;
    }

    public void execute() {
        String userId = user.getUserId();
        interactor.listMcpkit(userId);
    }
}
