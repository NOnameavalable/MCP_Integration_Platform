package csc207.mcp.interface_adapter.add_to_toolkit;

import csc207.mcp.interface_adapter.mcpkit.McpkitState;
import csc207.mcp.interface_adapter.mcpkit.McpkitViewModel;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputBoundary;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputData;
import java.util.Objects;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;

/** Updates the toolkit state and notifies its observers after an addition attempt. */
public final class AddToToolkitPresenter implements AddToToolkitOutputBoundary {
    private final McpkitViewModel viewModel;
    private final Consumer<Runnable> uiDispatcher;

    public AddToToolkitPresenter(McpkitViewModel viewModel) {
        this(viewModel, AddToToolkitPresenter::dispatchToJavaFx);
    }

    public AddToToolkitPresenter(McpkitViewModel viewModel, Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }

    @Override
    public void prepareSuccessView(AddToToolkitOutputData data) {
        Objects.requireNonNull(data);
        uiDispatcher.accept(() -> viewModel.setState(new McpkitState(
                "Added to toolkit successfully.", true, data.getMcpkitList())));
    }

    @Override
    public void prepareFailView(String errorMessage) {
        Objects.requireNonNull(errorMessage);
        uiDispatcher.accept(() -> viewModel.setState(new McpkitState(
                errorMessage, false, List.copyOf(viewModel.getState().getMcpkitList()))));
    }

    private static void dispatchToJavaFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
