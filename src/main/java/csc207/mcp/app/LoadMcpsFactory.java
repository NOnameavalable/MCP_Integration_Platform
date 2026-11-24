package csc207.mcp.app;

import csc207.mcp.interface_adapter.MarketViewModel;
import csc207.mcp.interface_adapter.load_mcps.LoadMcpsController;
import csc207.mcp.interface_adapter.load_mcps.LoadMcpsPresenter;
import csc207.mcp.use_case.load_mcps.LoadMcpsDataAccessInterface;
import csc207.mcp.use_case.load_mcps.LoadMcpsInteractor;
import java.util.function.Consumer;

/** Assembles catalog loading; the caller supplies the real data access and shared view model. */
public final class LoadMcpsFactory {
    private LoadMcpsFactory() { }

    public static LoadMcpsController create(LoadMcpsDataAccessInterface dataAccess,
                                            MarketViewModel viewModel) {
        return assemble(dataAccess, new LoadMcpsPresenter(viewModel));
    }

    /** For tests or an application-provided UI dispatcher. */
    public static LoadMcpsController create(LoadMcpsDataAccessInterface dataAccess,
                                            MarketViewModel viewModel,
                                            Consumer<Runnable> uiDispatcher) {
        return assemble(dataAccess, new LoadMcpsPresenter(viewModel, uiDispatcher));
    }

    private static LoadMcpsController assemble(LoadMcpsDataAccessInterface dataAccess,
                                               LoadMcpsPresenter presenter) {
        LoadMcpsInteractor interactor = new LoadMcpsInteractor(dataAccess, presenter);
        return new LoadMcpsController(interactor);
    }
}
