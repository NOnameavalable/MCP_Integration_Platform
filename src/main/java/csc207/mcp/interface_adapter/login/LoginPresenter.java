package csc207.mcp.interface_adapter.login;

import csc207.mcp.use_case.login.LoginOutputBoundary;
import csc207.mcp.use_case.login.LoginOutputData;

/**
 * Presenter skeleton for Login; will update LoginViewModel.
 */
public class LoginPresenter implements LoginOutputBoundary {
    @Override
    public void prepareSuccessView(LoginOutputData data) {
    }

    @Override
    public void prepareFailView(String errorMessage) {
    }
}
