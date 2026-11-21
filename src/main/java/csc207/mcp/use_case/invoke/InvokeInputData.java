package csc207.mcp.use_case.invoke;

public class InvokeInputData {

    private String key;
    private String request;

    public InvokeInputData(String key, String request) {
        this.key = key;
        this.request = request;
    }

    public String getKey() {
        return this.key;
    }

    public String getRequest() {
        return this.request;
    }
}
