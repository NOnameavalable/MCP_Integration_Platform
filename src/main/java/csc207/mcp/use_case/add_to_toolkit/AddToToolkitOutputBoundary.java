package csc207.mcp.use_case.add_to_toolkit;

/**
 * Defines how the add-to-toolkit interactor reports results to the presenter.
 */
public interface AddToToolkitOutputBoundary {
    /**
     * Presents a successful toolkit addition result.
     *
     * @param data the toolkit addition result
     */
    void prepareSuccessView(AddToToolkitOutputData data);

    /**
     * Presents a toolkit addition failure.
     *
     * @param errorMessage the reason the toolkit addition failed
     */
    void prepareFailView(String errorMessage);
}
