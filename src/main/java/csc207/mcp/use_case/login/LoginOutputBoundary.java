package csc207.mcp.use_case.login;

/**
 * Defines how the login interactor reports results to the presenter.
 */
public interface LoginOutputBoundary {
    /**
     * Presents a successful login result.
     *
     * @param data the authenticated user's account data
     */
    void prepareSuccessView(LoginOutputData data);

    /**
     * Presents a login failure.
     *
     * @param errorMessage the reason the login failed
     */
    void prepareFailView(String errorMessage);
}
