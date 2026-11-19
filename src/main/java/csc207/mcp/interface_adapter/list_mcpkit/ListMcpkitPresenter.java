package csc207.mcp.interface_adapter.list_mcpkit;

import csc207.mcp.interface_adapter.mcpkit.McpkitState;
import csc207.mcp.interface_adapter.mcpkit.McpkitViewModel;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitOutputBoundary;
import csc207.mcp.use_case.list_mcpkit.ListMcpkitOutputData;

public class ListMcpkitPresenter implements ListMcpkitOutputBoundary {
    private final McpkitViewModel mcpkitViewModel;

    public ListMcpkitPresenter(McpkitViewModel mcpkitViewModel) {
        this.mcpkitViewModel = mcpkitViewModel;
    }

    @Override
    public void prepareView(ListMcpkitOutputData outputdata) {
        mcpkitViewModel.setState(new McpkitState(outputdata.getMessage(),
                outputdata.isSuccess(), outputdata.getMcpkitList()));
    }

}
