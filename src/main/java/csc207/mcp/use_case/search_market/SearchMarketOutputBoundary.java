package csc207.mcp.use_case.search_market;

/**
 * Defines how the marketplace search interactor reports results to the presenter.
 */
public interface SearchMarketOutputBoundary {
    /**
     * Presents a successful marketplace search result.
     *
     * @param data the marketplace search result
     */
    void prepareSuccessView(SearchMarketOutputData data);

    /**
     * Presents a marketplace search failure.
     *
     * @param errorMessage the reason the marketplace search failed
     */
    void prepareFailView(String errorMessage);
}
