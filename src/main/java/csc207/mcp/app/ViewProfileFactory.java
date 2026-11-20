package csc207.mcp.app;

import csc207.mcp.interface_adapter.view_profile.*;
import csc207.mcp.use_case.view_profile.*;
import java.util.function.Consumer;

/** Composition helper for the account frontend and real profile DAO. */
public final class ViewProfileFactory {
    private ViewProfileFactory() { }
    public static ViewProfileController create(ViewProfileDataAccessInterface dataAccess, ViewProfileViewModel viewModel) {
        return new ViewProfileController(new ViewProfileInteractor(dataAccess, new ViewProfilePresenter(viewModel)));
    }
    public static ViewProfileController create(ViewProfileDataAccessInterface dataAccess, ViewProfileViewModel viewModel,
            Consumer<Runnable> uiDispatcher) {
        return new ViewProfileController(new ViewProfileInteractor(dataAccess, new ViewProfilePresenter(viewModel, uiDispatcher)));
    }
}
