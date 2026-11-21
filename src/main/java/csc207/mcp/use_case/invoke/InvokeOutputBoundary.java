package csc207.mcp.use_case.invoke;

/**
 * Gateway-module scaffold. TODO: implement InvokeOutputBoundary.
 */
public interface InvokeOutputBoundary {

    void prepareSuccessView(String statusCode, String response);

    void prepareFailView(String statusCode, String response);
}