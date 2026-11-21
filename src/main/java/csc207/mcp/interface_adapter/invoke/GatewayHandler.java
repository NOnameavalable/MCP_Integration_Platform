package csc207.mcp.interface_adapter.invoke;

import csc207.mcp.use_case.invoke.InvokeInputBoundary;
import csc207.mcp.use_case.invoke.InvokeInputData;

public class GatewayHandler {
    private final InvokeInputBoundary inputBoundary;

    public GatewayHandler(InvokeInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public InvokeResponse handleRequest(String key, String request) {
        InvokeInputData inputData = new InvokeInputData(key, request);
        InvokePresenter presenter = new InvokePresenter();
        inputBoundary.execute(inputData, presenter);
        return presenter.getResponse();
    }
}