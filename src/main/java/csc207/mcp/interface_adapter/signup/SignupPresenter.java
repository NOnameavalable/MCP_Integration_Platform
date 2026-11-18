package csc207.mcp.interface_adapter.signup;

import csc207.mcp.use_case.signup.SignupOutputBoundary;
import csc207.mcp.use_case.signup.SignupOutputData;
import java.util.Objects;

/** Converts use-case results to presentation state, then requests success navigation. */
public final class SignupPresenter implements SignupOutputBoundary {
    private final SignupViewModel viewModel;
    private final Runnable onSignupSuccess;

    public SignupPresenter(SignupViewModel viewModel, Runnable onSignupSuccess) {
        this.viewModel = Objects.requireNonNull(viewModel);
        this.onSignupSuccess = Objects.requireNonNull(onSignupSuccess);
    }

    @Override
    public void prepareSuccessView(SignupOutputData outputData) {
        viewModel.showSuccess(outputData.getUserId(), outputData.getUsername(), outputData.getEmail());
        onSignupSuccess.run();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.showFailure(Objects.requireNonNull(errorMessage));
    }
}
