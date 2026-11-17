package csc207.mcp.use_case.view_profile;

/** Reports profile-loading results without depending on a specific view. */
public interface ViewProfileOutputBoundary {

    /**
     * Presents the current user's username, email, and safe key summaries.
     * Output must contain no password hashes, key hashes, or raw API keys.
     * A successfully loaded profile with no keys is still a success.
     *
     * @param outputData the safe profile result
     */
    void prepareSuccessView(ViewProfileOutputData outputData);

    /**
     * Presents a failed profile load without exposing sensitive information.
     *
     * @param errorMessage a user-facing explanation
     */
    void prepareFailView(String errorMessage);
}
