package csc207.mcp.use_case.signup;

/** Reports signup results without depending on a specific user interface. */
public interface SignupOutputBoundary {

    /**
     * Presents a confirmed successful registration.
     *
     * @param outputData the non-sensitive registration result
     */
    void prepareSuccessView(SignupOutputData outputData);

    /**
     * Presents an unsuccessful registration.
     *
     * @param errorMessage a user-facing explanation without credentials or hashes
     */
    void prepareFailView(String errorMessage);
}
