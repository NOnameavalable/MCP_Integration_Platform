package csc207.mcp.interface_adapter.invoke;

public class InvokeResponse {
    private final String response;
    private final String statusCode;

    public InvokeResponse(String statusCode, String response) {
        this.response = response;
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return response;
    }

    public String getStatusCode() {
        return statusCode;
    }

}