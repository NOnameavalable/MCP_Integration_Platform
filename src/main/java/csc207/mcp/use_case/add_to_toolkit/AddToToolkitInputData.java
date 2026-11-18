package csc207.mcp.use_case.add_to_toolkit;

/**
 * Carries the user and server identifiers for a toolkit addition request.
 * Input validation and toolkit updates are handled by the add-to-toolkit interactor.
 */
public class AddToToolkitInputData {
    /** The authenticated user whose toolkit will receive the server. */
    private final String userId;

    /** The marketplace server selected for addition to the toolkit. */
    private final String serverId;

    /**
     * Creates the input data for a toolkit addition request.
     *
     * @param userId the user identifier supplied from the authenticated context
     * @param serverId the identifier of the selected marketplace server
     */
    public AddToToolkitInputData(String userId, String serverId) {
        this.userId = userId;
        this.serverId = serverId;
    }

    /**
     * Returns the identifier of the user whose toolkit will be updated.
     *
     * @return the user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the identifier of the server to add to the toolkit.
     *
     * @return the marketplace server identifier
     */
    public String getServerId() {
        return serverId;
    }
}
