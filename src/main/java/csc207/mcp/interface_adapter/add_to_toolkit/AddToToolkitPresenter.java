package csc207.mcp.interface_adapter.add_to_toolkit;

import csc207.mcp.interface_adapter.MarketViewModel;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputBoundary;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputData;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;

/** Presents addition success or failure through the marketplace view model. */
public final class AddToToolkitPresenter implements AddToToolkitOutputBoundary {
    private final MarketViewModel viewModel;
    private final Consumer<Runnable> uiDispatcher;

    public AddToToolkitPresenter(MarketViewModel viewModel) {
        this(viewModel, AddToToolkitPresenter::dispatchToJavaFx);
    }

    public AddToToolkitPresenter(MarketViewModel viewModel, Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }

    @Override
    public void prepareSuccessView(AddToToolkitOutputData data) {
        Objects.requireNonNull(data);
        uiDispatcher.accept(() -> viewModel.showSuccess(
                data.getUserId(), data.getServerId(), data.getServerName()));
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
