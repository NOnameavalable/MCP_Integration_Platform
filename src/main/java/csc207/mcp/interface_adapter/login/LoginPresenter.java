package csc207.mcp.interface_adapter.login;

import csc207.mcp.use_case.login.LoginOutputBoundary;
import csc207.mcp.use_case.login.LoginOutputData;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;

/** Updates JavaFX-observable login state and navigation on the UI thread. */
public final class LoginPresenter implements LoginOutputBoundary {
    private final LoginViewModel viewModel;
    private final Runnable onLoginSuccess;
    private final Consumer<Runnable> uiDispatcher;

    /** Requires a running JavaFX application when results are presented. */
    public LoginPresenter(LoginViewModel viewModel, Runnable onLoginSuccess) {
        this(viewModel, onLoginSuccess, LoginPresenter::dispatchToJavaFx);
    }

    /** Allows tests to dispatch results without starting the JavaFX toolkit. */
    public LoginPresenter(LoginViewModel viewModel, Runnable onLoginSuccess,
                          Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.onLoginSuccess = Objects.requireNonNull(onLoginSuccess);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }

    @Override
    public void prepareSuccessView(LoginOutputData data) {
        Objects.requireNonNull(data);
        uiDispatcher.accept(() -> {
            viewModel.showSuccess(data.getUserId(), data.getUsername());
            onLoginSuccess.run();
        });
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
