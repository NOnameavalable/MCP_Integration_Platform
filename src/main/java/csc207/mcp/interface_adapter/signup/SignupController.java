package csc207.mcp.interface_adapter.signup;

import csc207.mcp.use_case.signup.SignupInputBoundary;
import csc207.mcp.use_case.signup.SignupInputData;
import java.util.Objects;

/** Converts form values into a request without depending on a concrete interactor. */
public final class SignupController {
    private final SignupInputBoundary signup;

    public SignupController(SignupInputBoundary signup) {
        this.signup = Objects.requireNonNull(signup);
    }

    public void execute(String username, String email, String password, String repeatPassword) {
        signup.execute(new SignupInputData(username, email, password, repeatPassword));
    }
}
