package csc207.mcp.use_case.invoke;

public interface InvokeInputBoundary {
    void execute(InvokeInputData inputData, InvokeOutputBoundary presenter);
}