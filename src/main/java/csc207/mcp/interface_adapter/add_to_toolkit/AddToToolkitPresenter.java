package csc207.mcp.interface_adapter.add_to_toolkit;

import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputBoundary;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputData;

/**
 * Presenter skeleton for AddToToolkit; will update the shared MarketViewModel.
 */
public class AddToToolkitPresenter implements AddToToolkitOutputBoundary {
    @Override
    public void prepareSuccessView(AddToToolkitOutputData data) {
    }

    @Override
    public void prepareFailView(String errorMessage) {
    }
}
