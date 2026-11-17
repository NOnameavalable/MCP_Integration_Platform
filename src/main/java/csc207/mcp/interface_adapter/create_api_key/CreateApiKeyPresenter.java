package csc207.mcp.interface_adapter.create_api_key;

import csc207.mcp.use_case.create_api_key.CreateApiKeyOutputBoundary;
import csc207.mcp.use_case.create_api_key.CreateApiKeyOutputData;

/** Account-module scaffold. Key-creation presentation is not implemented yet. */
public class CreateApiKeyPresenter implements CreateApiKeyOutputBoundary {

    @Override
    public void prepareSuccessView(CreateApiKeyOutputData outputData) {
        // TODO: coordinate one-time key display and profile refresh with the UI.
        throw new UnsupportedOperationException("API key presentation is not implemented yet.");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // TODO: make the failure message available to the account view.
        throw new UnsupportedOperationException("API key presentation is not implemented yet.");
    }
}
