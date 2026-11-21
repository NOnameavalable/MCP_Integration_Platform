package csc207.mcp.interface_adapter.search_market;

import csc207.mcp.use_case.search_market.SearchMarketInputBoundary;
import csc207.mcp.use_case.search_market.SearchMarketInputData;
import java.util.Objects;

/** Converts search form values into a request for the search use case. */
public final class SearchMarketController {
    private final SearchMarketInputBoundary searchMarketInteractor;

    public SearchMarketController(SearchMarketInputBoundary searchMarketInteractor) {
        this.searchMarketInteractor = Objects.requireNonNull(searchMarketInteractor);
    }

    public void execute(String keyword) {
        searchMarketInteractor.excute(new SearchMarketInputData(keyword));
    }
}
