package csc207.mcp.use_case.load_mcps;

public interface LoadMcpsOutputBoundary {
    void prepareSuccessView(LoadMcpsOutputData data);

    void prepareFailView(String errorMessage);
}
