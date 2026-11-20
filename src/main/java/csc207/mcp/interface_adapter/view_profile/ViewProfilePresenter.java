package csc207.mcp.interface_adapter.view_profile;

import csc207.mcp.use_case.view_profile.ViewProfileOutputBoundary;
import csc207.mcp.use_case.view_profile.ViewProfileOutputData;
import javafx.application.Platform;
import java.util.Objects;
import java.util.function.Consumer;

/** Presents safe profile output and user-facing error messages. */
public final class ViewProfilePresenter implements ViewProfileOutputBoundary {
    private final ViewProfileViewModel viewModel;
    private final Consumer<Runnable> uiDispatcher;
    public ViewProfilePresenter(ViewProfileViewModel viewModel) {
        this(viewModel, action -> {
            if (Platform.isFxApplicationThread()) { action.run(); } else { Platform.runLater(action); }
        });
    }
    public ViewProfilePresenter(ViewProfileViewModel viewModel, Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }
    @Override public void prepareSuccessView(ViewProfileOutputData outputData) { present(outputData); }
    @Override public void prepareFailView(String errorMessage) {
        present(ViewProfileOutputData.failure(Objects.requireNonNull(errorMessage)));
    }
    private void present(ViewProfileOutputData outputData) {
        Objects.requireNonNull(outputData);
        uiDispatcher.accept(() -> viewModel.setResult(outputData));
    }
}
