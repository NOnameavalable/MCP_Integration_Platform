package csc207.mcp.use_case.login;

/**
 * Defines the entry point for the login use case.
 * Controllers depend on this interface, which is implemented by the interactor.
 */
public interface LoginInputBoundary {
    /**
     * Requests user authentication using the supplied login input data.
     *
     * @param data the input data for the login request
     */
    void excute(LoginInputData data);
}
