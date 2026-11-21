package csc207.mcp.use_case.create_api_key;

/** Entry point through which a controller requests a new API key. */
public interface CreateApiKeyInputBoundary {

    /**
     * Processes a key-creation request and reports through the output boundary.
     * InputData must carry the authenticated user ID supplied by the application,
     * not an arbitrary user-entered owner ID.
     *
     * @param inputData the key-creation request
     */
    void execute(CreateApiKeyInputData inputData);
}
