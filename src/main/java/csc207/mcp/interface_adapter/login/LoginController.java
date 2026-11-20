package csc207.mcp.interface_adapter.login;

import csc207.mcp.use_case.login.LoginInputBoundary;
import csc207.mcp.use_case.login.LoginInputData;
import java.util.Objects;

/** Converts login form values into a request for the login use case. */
public final class LoginController {
    private final LoginInputBoundary loginInteractor;

    public LoginController(LoginInputBoundary loginInteractor) {
        this.loginInteractor = Objects.requireNonNull(loginInteractor);
    }

    public void execute(String identifier, String password) {
        LoginInputData inputData = new LoginInputData(identifier, password);
        loginInteractor.excute(inputData);
    }
}
