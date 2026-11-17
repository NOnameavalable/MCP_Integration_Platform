package csc207.mcp.interface_adapter.view_profile;

import csc207.mcp.use_case.view_profile.ViewProfileOutputBoundary;
import csc207.mcp.use_case.view_profile.ViewProfileOutputData;

/** Account-module scaffold. Profile presentation is not implemented yet. */
public class ViewProfilePresenter implements ViewProfileOutputBoundary {

    @Override
    public void prepareSuccessView(ViewProfileOutputData outputData) {
        // TODO: update AccountViewModel with username, email, and key summaries.
        throw new UnsupportedOperationException("Profile presentation is not implemented yet.");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // TODO: make the load failure available to the account view.
        throw new UnsupportedOperationException("Profile presentation is not implemented yet.");
    }
}
