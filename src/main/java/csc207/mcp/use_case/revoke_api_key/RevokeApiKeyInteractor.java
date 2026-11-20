package csc207.mcp.use_case.revoke_api_key;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.use_case.account.CurrentUserContext;
import java.util.Objects;
import java.util.Optional;

/** Inspect ownership, then request an ownership-checked persistent revocation. */
public final class RevokeApiKeyInteractor implements RevokeApiKeyInputBoundary {
    private final RevokeApiKeyDataAccessInterface dataAccess;
    private final RevokeApiKeyOutputBoundary presenter;
    private final CurrentUserContext currentUser;
    public RevokeApiKeyInteractor(RevokeApiKeyDataAccessInterface dataAccess,
            RevokeApiKeyOutputBoundary presenter, CurrentUserContext currentUser) {
        this.dataAccess = Objects.requireNonNull(dataAccess);
        this.presenter = Objects.requireNonNull(presenter);
        this.currentUser = Objects.requireNonNull(currentUser);
    }
    @Override public void execute(RevokeApiKeyInputData input) {
        if (input == null || input.getKeyId() == null || input.getKeyId().isBlank()) {
            presenter.prepareFailView("Please select an API key."); return;
        }
        String owner;
        try { owner = currentUser.getAuthenticatedUserId(); }
        catch (RuntimeException failure) { presenter.prepareFailView("Unable to verify the current session."); return; }
        if (owner == null || owner.isBlank()) { presenter.prepareFailView("Please log in before revoking an API key."); return; }
        Optional<ApiKey> found;
        try { found = Objects.requireNonNull(dataAccess.findById(input.getKeyId())); }
        catch (RuntimeException failure) {
            presenter.prepareFailView("The API key could not be loaded. Please try again."); return;
        }
        if (found.isEmpty() || !found.get().belongsTo(owner)) {
            presenter.prepareFailView("The API key is unavailable for this account."); return;
        }
        boolean alreadyRevoked = !found.get().isActive();
        if (!alreadyRevoked) {
            try { dataAccess.revoke(input.getKeyId(), owner); }
            catch (IllegalArgumentException conflict) {
                presenter.prepareFailView("The API key is unavailable for this account."); return;
            } catch (IllegalStateException failure) {
                presenter.prepareFailView("The API key could not be revoked. Please try again."); return;
            }
        }
        presenter.prepareSuccessView(new RevokeApiKeyOutputData(input.getKeyId(), alreadyRevoked));
    }
}
