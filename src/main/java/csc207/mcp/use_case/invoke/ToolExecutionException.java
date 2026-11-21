package csc207.mcp.use_case.invoke;

public class ToolExecutionException extends RuntimeException {
    private final String statusCode;

    public ToolExecutionException(String message, String statuscode) {
        super(message);
        this.statusCode = statuscode;
    }

    public String getStatusCode() {
        return this.statusCode;
    }
}
