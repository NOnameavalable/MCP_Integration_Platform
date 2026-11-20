package csc207.mcp.app;

import csc207.mcp.use_case.revoke_api_key.*;
import csc207.mcp.interface_adapter.revoke_api_key.*;
import java.util.function.Consumer;

/** Frontend supplies a real DAO, observable model, and UI callback. */
public final class RevokeApiKeyFactory {
    private RevokeApiKeyFactory() { }
    public static RevokeApiKeyController create(RevokeApiKeyDataAccessInterface dataAccess,
            RevokeApiKeyViewModel viewModel, Runnable onRevoked) {
        return assemble(dataAccess, new RevokeApiKeyPresenter(viewModel, onRevoked));
    }
    public static RevokeApiKeyController create(RevokeApiKeyDataAccessInterface dataAccess,
            RevokeApiKeyViewModel viewModel, Runnable onRevoked,
            Consumer<Runnable> uiDispatcher) {
        return assemble(dataAccess, new RevokeApiKeyPresenter(viewModel, onRevoked, uiDispatcher));
    }
    private static RevokeApiKeyController assemble(RevokeApiKeyDataAccessInterface dataAccess,
            RevokeApiKeyPresenter presenter) {
        return new RevokeApiKeyController(new RevokeApiKeyInteractor(dataAccess, presenter));
    }
}
