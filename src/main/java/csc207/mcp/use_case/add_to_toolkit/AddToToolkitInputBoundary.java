package csc207.mcp.use_case.add_to_toolkit;

/**
 * Defines the entry point for the add-to-toolkit use case.
 * Controllers depend on this interface, which is implemented by the interactor.
 */
public interface AddToToolkitInputBoundary {
    /**
     * Requests the addition of a marketplace server to a user's toolkit.
     *
     * @param data the input data for the toolkit addition
     */
    void excute(AddToToolkitInputData data);
}
