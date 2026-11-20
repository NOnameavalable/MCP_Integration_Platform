package csc207.mcp.app;

import csc207.mcp.use_case.create_api_key.*;
import csc207.mcp.interface_adapter.create_api_key.*;
import csc207.mcp.use_case.account.CurrentUserContext;
import java.util.function.Consumer;
import csc207.mcp.frameworks_and_drivers.security.SecureApiKeyCredentials;

/** Frontend supplies a real DAO, trusted session, observable model, and UI callback. */
public final class CreateApiKeyFactory {
    private CreateApiKeyFactory() { }
    public static CreateApiKeyController create(CreateApiKeyDataAccessInterface dataAccess,
            CurrentUserContext currentUser, CreateApiKeyViewModel viewModel, Consumer<CreateApiKeyOutputData> onCreated) {
        return assemble(dataAccess, currentUser, new CreateApiKeyPresenter(viewModel, onCreated));
    }
    public static CreateApiKeyController create(CreateApiKeyDataAccessInterface dataAccess,
            CurrentUserContext currentUser, CreateApiKeyViewModel viewModel, Consumer<CreateApiKeyOutputData> onCreated,
            Consumer<Runnable> uiDispatcher) {
        return assemble(dataAccess, currentUser, new CreateApiKeyPresenter(viewModel, onCreated, uiDispatcher));
    }
    private static CreateApiKeyController assemble(CreateApiKeyDataAccessInterface dataAccess,
            CurrentUserContext currentUser, CreateApiKeyPresenter presenter) {
        return new CreateApiKeyController(new CreateApiKeyInteractor(dataAccess, presenter, currentUser, new SecureApiKeyCredentials()));
    }
}
