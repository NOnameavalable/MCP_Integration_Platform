package csc207.mcp.interface_adapter.revoke_api_key;

import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyOutputBoundary;
import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyOutputData;

/** Account-module scaffold. Revocation presentation is not implemented yet. */
public class RevokeApiKeyPresenter implements RevokeApiKeyOutputBoundary {

    @Override
    public void prepareSuccessView(RevokeApiKeyOutputData outputData) {
        // TODO: display the confirmed result and coordinate profile refresh.
        throw new UnsupportedOperationException("API key revocation presentation is not implemented yet.");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // TODO: make the failure available to the account view.
        throw new UnsupportedOperationException("API key revocation presentation is not implemented yet.");
    }
}
