package csc207.mcp.use_case.signup;

/** Entry point through which a controller requests account registration. */
public interface SignupInputBoundary {

    /**
     * Processes a signup request and reports its result through the output boundary.
     *
     * @param inputData the submitted registration information
     */
    void execute(SignupInputData inputData);
}
