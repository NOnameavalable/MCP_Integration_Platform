package csc207.mcp.app;

import csc207.mcp.frameworks_and_drivers.search.CommonsTextNameScorer;
import csc207.mcp.interface_adapter.search_market.SearchMarketController;
import csc207.mcp.interface_adapter.search_market.SearchMarketPresenter;
import csc207.mcp.interface_adapter.search_market.SearchMarketViewModel;
import csc207.mcp.use_case.search_market.SearchMarketDataAccessInterface;
import csc207.mcp.use_case.search_market.SearchMarketInteractor;
import java.util.function.Consumer;

/** Assembles name scoring and search presentation; the caller supplies the real data access. */
public final class SearchMarketFactory {
    private SearchMarketFactory() { }

    public static SearchMarketController create(SearchMarketDataAccessInterface dataAccess,
                                                SearchMarketViewModel viewModel) {
        return assemble(dataAccess, new SearchMarketPresenter(viewModel));
    }

    /** For tests or an application-provided UI dispatcher. */
    public static SearchMarketController create(SearchMarketDataAccessInterface dataAccess,
                                                SearchMarketViewModel viewModel,
                                                Consumer<Runnable> uiDispatcher) {
        return assemble(dataAccess, new SearchMarketPresenter(viewModel, uiDispatcher));
    }

    private static SearchMarketController assemble(SearchMarketDataAccessInterface dataAccess,
                                                   SearchMarketPresenter presenter) {
        SearchMarketInteractor interactor = new SearchMarketInteractor(
                dataAccess, presenter, new CommonsTextNameScorer());
        return new SearchMarketController(interactor);
    }
}
