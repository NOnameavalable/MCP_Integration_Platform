package csc207.mcp.use_case.view_profile;

/** Entry point for a controller to request the current user's profile. */
public interface ViewProfileInputBoundary {

    /**
     * Loads profile information and reports through the output boundary.
     * The user identity must come from authenticated context, not from
     * an arbitrary user-entered profile-owner identifier.
     *
     * @param inputData the profile-view request
     */
    void execute(ViewProfileInputData inputData);
}
