package csc207.mcp.use_case.view_profile;

/** Request identity must be supplied from trusted login state, never an editable owner field. */
public final class ViewProfileInputData {
    private final String userId;
    public ViewProfileInputData(String userId) { this.userId = userId; }
    public String getUserId() { return userId; }
}
