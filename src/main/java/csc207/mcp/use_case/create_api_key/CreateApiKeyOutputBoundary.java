package csc207.mcp.use_case.create_api_key;

/** Reports key-creation results without depending on a specific view. */
public interface CreateApiKeyOutputBoundary {

    /**
     * Presents a confirmed successful creation, only after saving succeeds.
     * The result may include the raw key for one-time display; the presenter
     * must not log it or retain it in the normal profile/key-summary list.
     *
     * @param outputData the one-time creation result
     */
    void prepareSuccessView(CreateApiKeyOutputData outputData);

    /**
     * Presents a failed creation without exposing credentials or hashes.
     *
     * @param errorMessage a user-facing explanation
     */
    void prepareFailView(String errorMessage);
}
