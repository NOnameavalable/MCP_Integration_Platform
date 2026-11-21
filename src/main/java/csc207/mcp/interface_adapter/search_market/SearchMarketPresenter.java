package csc207.mcp.interface_adapter.search_market;

import csc207.mcp.use_case.search_market.SearchMarketOutputBoundary;
import csc207.mcp.use_case.search_market.SearchMarketOutputData;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;

/** Updates the dedicated search ViewModel on the JavaFX application thread. */
public final class SearchMarketPresenter implements SearchMarketOutputBoundary {
    private final SearchMarketViewModel viewModel;
    private final Consumer<Runnable> uiDispatcher;

    /** Requires a running JavaFX application when results are presented. */
    public SearchMarketPresenter(SearchMarketViewModel viewModel) {
        this(viewModel, SearchMarketPresenter::dispatchToJavaFx);
    }

    /** Allows deterministic UI dispatch in tests without starting the toolkit. */
    public SearchMarketPresenter(SearchMarketViewModel viewModel, Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }

    @Override
    public void prepareSuccessView(SearchMarketOutputData data) {
        Objects.requireNonNull(data);
        uiDispatcher.accept(() -> viewModel.showSuccess(data.getKeyword(), data.getResults()));
    }

    @Override
    public void prepareFailView(String errorMessage) {
        Objects.requireNonNull(errorMessage);
        uiDispatcher.accept(() -> viewModel.showFailure(errorMessage));
    }

    private static void dispatchToJavaFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
