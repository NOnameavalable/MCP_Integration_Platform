package csc207.mcp.use_case.search_market;

/**
 * Carries the keyword for a marketplace search request.
 * Input validation and search processing are handled by the search interactor.
 */
public class SearchMarketInputData {
    /** The keyword entered to search for marketplace servers. */
    private final String keyword;

    /**
     * Creates the input data for a marketplace search request.
     *
     * @param keyword the keyword used to search for marketplace servers
     */
    public SearchMarketInputData(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword supplied for the marketplace search.
     *
     * @return the search keyword
     */
    public String getKeyword() {
        return keyword;
    }
}
