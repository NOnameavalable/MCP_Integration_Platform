package csc207.mcp.use_case.create_api_key;

/** Entry point through which a controller requests a new API key. */
public interface CreateApiKeyInputBoundary {

    /**
     * Processes a key-creation request and reports through the output boundary.
     * Ownership must be derived from the authenticated user context,
     * not trusted from an arbitrary user-entered owner ID.
     *
     * @param inputData the key-creation request
     */
    void execute(CreateApiKeyInputData inputData);
}
