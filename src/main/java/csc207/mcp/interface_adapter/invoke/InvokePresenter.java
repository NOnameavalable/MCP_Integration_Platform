package csc207.mcp.interface_adapter.invoke;

import csc207.mcp.use_case.invoke.InvokeOutputBoundary;

/**
 * Gateway-module scaffold. TODO: implement InvokePresenter.
 */
public class InvokePresenter implements InvokeOutputBoundary {
    InvokeResponse response;

    @Override
    public void prepareSuccessView(String statusCode, String response) {
        this.response = new InvokeResponse(statusCode, response);
    }

    @Override
    public void prepareFailView(String statusCode, String response) {
        this.response = new InvokeResponse(statusCode, response);
    }

    public InvokeResponse getResponse() {
        return this.response;
    }
}
