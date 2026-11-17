package csc207.mcp.use_case.search_market;

/**
 * Defines the entry point for the marketplace search use case.
 * Controllers depend on this interface, which is implemented by the interactor.
 */
public interface SearchMarketInputBoundary {
    /**
     * Requests a search for marketplace servers using the supplied input data.
     *
     * @param data the input data for the marketplace search
     */
    void excute(SearchMarketInputData data);
}
