package csc207.mcp.use_case.revoke_api_key;

/** Reports revocation results independently of a specific user interface. */
public interface RevokeApiKeyOutputBoundary {

    /**
     * Presents a confirmed revoked state, only after persistence succeeds
     * or an already revoked state has been confirmed.
     *
     * @param outputData the key identifier and confirmed revocation result
     */
    void prepareSuccessView(RevokeApiKeyOutputData outputData);

    /**
     * Presents an unsuccessful revocation without exposing credentials or hashes.
     *
     * @param errorMessage a user-facing explanation
     */
    void prepareFailView(String errorMessage);
}
