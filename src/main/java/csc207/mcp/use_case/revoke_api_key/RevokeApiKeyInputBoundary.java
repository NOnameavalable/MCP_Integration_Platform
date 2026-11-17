package csc207.mcp.use_case.revoke_api_key;

/** Entry point through which a controller requests API key revocation. */
public interface RevokeApiKeyInputBoundary {

    /**
     * Processes the selected key's revocation and reports through the output boundary.
     * Ownership must be verified against authenticated user context, not trusted
     * from an arbitrary user-entered owner ID.
     *
     * @param inputData the request identifying the selected key
     */
    void execute(RevokeApiKeyInputData inputData);
}
