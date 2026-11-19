package csc207.mcp.interface_adapter.list_mcpkit;

import csc207.mcp.entity.User;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitInputBoundary;

/**
 * Controller for the list-mcpkit use case.
 */
public class ListMcpkitController {
    private final ListMcpkitInputBoundary interactor;

    public ListMcpkitController(ListMcpkitInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(User user) {
        String userId = user.getUserId();
        interactor.listMcpkit(userId);
    }
}
