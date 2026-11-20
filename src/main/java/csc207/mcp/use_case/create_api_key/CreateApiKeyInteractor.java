package csc207.mcp.use_case.create_api_key;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.use_case.account.CurrentUserContext;
import java.time.Clock;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/** Generate and save a credential record before revealing the secret. */
public final class CreateApiKeyInteractor implements CreateApiKeyInputBoundary {
    private final CreateApiKeyDataAccessInterface dataAccess;
    private final CreateApiKeyOutputBoundary presenter;
    private final CurrentUserContext currentUser;
    private final ApiKeyCredentials credentials;
    private final Clock clock;
    private final Supplier<String> keyIdGenerator;

    public CreateApiKeyInteractor(CreateApiKeyDataAccessInterface dataAccess,
            CreateApiKeyOutputBoundary presenter, CurrentUserContext currentUser, ApiKeyCredentials credentials) {
        this(dataAccess, presenter, currentUser, credentials, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }
    public CreateApiKeyInteractor(CreateApiKeyDataAccessInterface dataAccess,
            CreateApiKeyOutputBoundary presenter, CurrentUserContext currentUser, ApiKeyCredentials credentials,
            Clock clock, Supplier<String> keyIdGenerator) {
        this.dataAccess = Objects.requireNonNull(dataAccess);
        this.presenter = Objects.requireNonNull(presenter);
        this.currentUser = Objects.requireNonNull(currentUser);
        this.credentials = Objects.requireNonNull(credentials);
        this.clock = Objects.requireNonNull(clock);
        this.keyIdGenerator = Objects.requireNonNull(keyIdGenerator);
    }
    @Override public void execute(CreateApiKeyInputData input) {
        if (input == null) { presenter.prepareFailView("Please provide a key creation request."); return; }
        String owner;
        try { owner = currentUser.getAuthenticatedUserId(); }
        catch (RuntimeException failure) { presenter.prepareFailView("Unable to verify the current session."); return; }
        if (owner == null || owner.isBlank()) { presenter.prepareFailView("Please log in before creating an API key."); return; }
        ApiKey key;
        String secret;
        try {
            secret = credentials.generateSecret();
            if (secret == null || secret.length() < 32 || secret.isBlank()) {
                throw new IllegalStateException("Invalid generated credential");
            }
            String hash = credentials.hashSecret(secret);
            if (secret.equals(hash)) { throw new IllegalStateException("Secret must not be stored as its hash"); }
            key = new ApiKey(keyIdGenerator.get(), owner, hash, secret.substring(0, 12), clock.instant());
        } catch (RuntimeException failure) {
            presenter.prepareFailView("The API key could not be prepared. Please try again."); return;
        }
        try { dataAccess.save(key); }
        catch (IllegalArgumentException conflict) {
            presenter.prepareFailView("The API key conflicts with an existing record. Please try again."); return;
        } catch (IllegalStateException failure) {
            presenter.prepareFailView("The API key could not be saved. Please try again."); return;
        }
        presenter.prepareSuccessView(new CreateApiKeyOutputData(
                key.getKeyId(), key.getDisplayPrefix(), key.getCreatedAt(), secret));
    }
}
