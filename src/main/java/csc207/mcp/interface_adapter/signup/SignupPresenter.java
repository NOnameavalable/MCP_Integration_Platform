package csc207.mcp.interface_adapter.signup;

import csc207.mcp.use_case.signup.SignupOutputBoundary;
import csc207.mcp.use_case.signup.SignupOutputData;

/** Account-module scaffold. Signup presentation is not implemented yet. */
public class SignupPresenter implements SignupOutputBoundary {

    @Override
    public void prepareSuccessView(SignupOutputData outputData) {
        // TODO: update SignupViewModel and coordinate navigation after success.
        throw new UnsupportedOperationException("Signup presentation is not implemented yet.");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // TODO: make the error available to the signup view through its view model.
        throw new UnsupportedOperationException("Signup presentation is not implemented yet.");
    }
}
