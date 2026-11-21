package csc207.mcp.use_case.revoke_api_key;

/** Entry point through which a controller requests API key revocation. */
public interface RevokeApiKeyInputBoundary {

    /**
     * Processes the selected key's revocation and reports through the output boundary.
     * Ownership is checked against the authenticated user ID in InputData.
     * The application must supply this ID from trusted login state.
     *
     * @param inputData the request identifying the selected key
     */
    void execute(RevokeApiKeyInputData inputData);
}
