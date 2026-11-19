package csc207.mcp.app;

import csc207.mcp.frameworks_and_drivers.security.Pbkdf2PasswordHasher;
import csc207.mcp.interface_adapter.signup.SignupController;
import csc207.mcp.interface_adapter.signup.SignupPresenter;
import csc207.mcp.interface_adapter.signup.SignupViewModel;
import csc207.mcp.use_case.signup.SignupDataAccessInterface;
import csc207.mcp.use_case.signup.SignupInteractor;
import java.util.function.Consumer;

/** Composition helper; frontend supplies the real DAO, view model, and navigation action. */
public final class SignupFactory {
    private SignupFactory() { }

    public static SignupController create(SignupDataAccessInterface dataAccess,
                                          SignupViewModel viewModel, Runnable showLoginView) {
        SignupPresenter presenter = new SignupPresenter(viewModel, showLoginView);
        return assemble(dataAccess, presenter);
    }
    /** For tests or an application-provided UI dispatcher. */
    public static SignupController create(SignupDataAccessInterface dataAccess,
                                          SignupViewModel viewModel, Runnable showLoginView,
                                          Consumer<Runnable> uiDispatcher) {
        return assemble(dataAccess, new SignupPresenter(viewModel, showLoginView, uiDispatcher));
    }

    private static SignupController assemble(SignupDataAccessInterface dataAccess,
                                             SignupPresenter presenter) {
        SignupInteractor interactor = new SignupInteractor(dataAccess, presenter, new Pbkdf2PasswordHasher());
        return new SignupController(interactor);
    }
}
