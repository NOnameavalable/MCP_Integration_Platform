package csc207.mcp.interface_adapter.create_api_key;

import csc207.mcp.use_case.create_api_key.CreateApiKeyOutputBoundary;
import csc207.mcp.use_case.create_api_key.CreateApiKeyOutputData;
import javafx.application.Platform;
import java.util.Objects;
import java.util.function.Consumer;

/** The callback displays the secret once and must release it when the dialog closes.
 * The presenter and observable state do not retain the creation DTO after dispatch. */
public final class CreateApiKeyPresenter implements CreateApiKeyOutputBoundary {
    private final CreateApiKeyViewModel viewModel;
    private final Consumer<CreateApiKeyOutputData> onCreated;
    private final Consumer<Runnable> uiDispatcher;
    public CreateApiKeyPresenter(CreateApiKeyViewModel viewModel, Consumer<CreateApiKeyOutputData> onCreated) {
        this(viewModel, onCreated, CreateApiKeyPresenter::dispatchToJavaFx);
    }
    public CreateApiKeyPresenter(CreateApiKeyViewModel viewModel, Consumer<CreateApiKeyOutputData> onCreated, Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.onCreated = Objects.requireNonNull(onCreated);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }
    @Override public void prepareSuccessView(CreateApiKeyOutputData output) {
        Objects.requireNonNull(output);
        uiDispatcher.accept(() -> {
            viewModel.showSuccess(output.getKeyId(), output.getDisplayPrefix(), output.getCreatedAt());
            onCreated.accept(output);
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
