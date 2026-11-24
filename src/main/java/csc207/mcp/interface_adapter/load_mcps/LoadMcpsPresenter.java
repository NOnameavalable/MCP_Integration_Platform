package csc207.mcp.interface_adapter.load_mcps;

import csc207.mcp.interface_adapter.MarketViewModel;
import csc207.mcp.use_case.load_mcps.LoadMcpsOutputBoundary;
import csc207.mcp.use_case.load_mcps.LoadMcpsOutputData;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;

/** Updates the shared MarketViewModel on the JavaFX application thread. */
public final class LoadMcpsPresenter implements LoadMcpsOutputBoundary {
    private final MarketViewModel viewModel;
    private final Consumer<Runnable> uiDispatcher;

    /** Requires a running JavaFX application when results are presented. */
    public LoadMcpsPresenter(MarketViewModel viewModel) {
        this(viewModel, LoadMcpsPresenter::dispatchToJavaFx);
    }

    /** Allows deterministic UI dispatch in tests without starting the toolkit. */
    public LoadMcpsPresenter(MarketViewModel viewModel, Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }

    @Override
    public void prepareSuccessView(LoadMcpsOutputData data) {
        Objects.requireNonNull(data);
        uiDispatcher.accept(() -> viewModel.showSuccess(data.getMcps()));
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
