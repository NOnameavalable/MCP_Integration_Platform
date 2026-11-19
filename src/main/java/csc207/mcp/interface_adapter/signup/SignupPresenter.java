package csc207.mcp.interface_adapter.signup;

import csc207.mcp.use_case.signup.SignupOutputBoundary;
import csc207.mcp.use_case.signup.SignupOutputData;
import javafx.application.Platform;
import java.util.Objects;
import java.util.function.Consumer;

/** Updates JavaFX-observable state and navigation on the UI thread. */
public final class SignupPresenter implements SignupOutputBoundary {
    private final SignupViewModel viewModel;
    private final Runnable onSignupSuccess;
    private final Consumer<Runnable> uiDispatcher;

    /** Requires a running JavaFX application when results are presented. */
    public SignupPresenter(SignupViewModel viewModel, Runnable onSignupSuccess) {
        this(viewModel, onSignupSuccess, SignupPresenter::dispatchToJavaFx);
    }

    /** Allows deterministic dispatch in tests without starting the JavaFX toolkit. */
    public SignupPresenter(SignupViewModel viewModel, Runnable onSignupSuccess,
                           Consumer<Runnable> uiDispatcher) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.onSignupSuccess = Objects.requireNonNull(onSignupSuccess);
        this.uiDispatcher = Objects.requireNonNull(uiDispatcher);
    }

    @Override
    public void prepareSuccessView(SignupOutputData outputData) {
        Objects.requireNonNull(outputData);
        uiDispatcher.accept(() -> {
            viewModel.showSuccess(outputData.getUserId(), outputData.getUsername(), outputData.getEmail());
            onSignupSuccess.run();
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
