package csc207.mcp.use_case.revoke_api_key;

/** Account-module scaffold. Revocation business logic is not implemented yet. */
public class RevokeApiKeyInteractor implements RevokeApiKeyInputBoundary {

    @Override
    public void execute(RevokeApiKeyInputData inputData) {
        // TODO: identify the authenticated user, check key ownership/status,
        // persist revocation, and report the confirmed result through the output boundary.
        throw new UnsupportedOperationException("API key revocation is not implemented yet.");
    }
}
