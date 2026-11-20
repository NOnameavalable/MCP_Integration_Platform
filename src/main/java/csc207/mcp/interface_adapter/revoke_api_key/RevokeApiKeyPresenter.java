package csc207.mcp.interface_adapter.revoke_api_key;

import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyOutputBoundary;
import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyOutputData;
import javafx.application.Platform;
import java.util.Objects;
import java.util.function.Consumer;

/** Publishes confirmed results on the JavaFX thread. Callbacks handle display/refresh. */
public final class RevokeApiKeyPresenter implements RevokeApiKeyOutputBoundary {
    private final RevokeApiKeyViewModel viewModel;
    private final Runnable onRevoked;
    private final Consumer<Runnable> uiDispatcher;
    public RevokeApiKeyPresenter(RevokeApiKeyViewModel viewModel, Runnable onRevoked) {
        this(viewModel, onRevoked, RevokeApiKeyPresenter::dispatchToJavaFx);
    }
    public RevokeApiKeyPresenter(RevokeApiKeyViewModel viewModel, Runnable onRevoked, Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.onRevoked = Objects.requireNonNull(onRevoked);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }
    @Override public void prepareSuccessView(RevokeApiKeyOutputData output) {
        Objects.requireNonNull(output);
        uiDispatcher.accept(() -> {
            viewModel.showSuccess(output.getKeyId(), output.isAlreadyRevoked());
            onRevoked.run();
        });
    }
    @Override public void prepareFailView(String errorMessage) {
        Objects.requireNonNull(errorMessage);
        uiDispatcher.accept(() -> viewModel.showFailure(errorMessage));
    }
    private static void dispatchToJavaFx(Runnable action) {
        if (Platform.isFxApplicationThread()) { action.run(); } else { Platform.runLater(action); }
    }
}
