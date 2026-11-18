package csc207.mcp.interface_adapter.search_market;

import csc207.mcp.use_case.search_market.SearchMarketOutputBoundary;
import csc207.mcp.use_case.search_market.SearchMarketOutputData;

/**
 * Presenter skeleton for SearchMarket; will update the shared MarketViewModel.
 */
public class SearchMarketPresenter implements SearchMarketOutputBoundary {
    @Override
    public void prepareSuccessView(SearchMarketOutputData data) {
    }

    @Override
    public void prepareFailView(String errorMessage) {
    }
}
