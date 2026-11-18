package csc207.mcp.entity;

import java.time.LocalDateTime;

/**
 * Gateway-module scaffold. TODO: implement CallRecord.
 */
public class CallRecord {
    private int useid;
    private String mcpServerName;
    private String toolName;
    private String arguments;
    private String result;
    private LocalDateTime localDateTime;

    public CallRecord(int useid, String mcpServerName, String toolName, String arguments) {
        this.useid = useid;
        this.mcpServerName = mcpServerName;
        this.toolName = toolName;
        this.arguments = arguments;
        this.localDateTime = LocalDateTime.now();
    }

    public int getUseid() {
        return this.useid;
    }

    public LocalDateTime getTime() {
        return this.localDateTime;
    }

    public String getMcpServerName() {
        return this.mcpServerName;
    }

    public String getToolName() {
        return this.toolName;
    }

    public String getArguments() {
        return this.arguments;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
