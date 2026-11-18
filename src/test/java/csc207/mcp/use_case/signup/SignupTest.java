package csc207.mcp.use_case.signup;

import csc207.mcp.app.SignupFactory;
import csc207.mcp.entity.User;
import csc207.mcp.frameworks_and_drivers.security.Pbkdf2PasswordHasher;
import csc207.mcp.interface_adapter.signup.SignupController;
import csc207.mcp.interface_adapter.signup.SignupPresenter;
import csc207.mcp.interface_adapter.signup.SignupViewModel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Dependency-free test runner. Run main; no network, database, or UI is required. */
public final class SignupTest {
    private static int checks;
    private SignupTest() { }

    public static void main(String[] args) {
        successfulRegistration();
        validationFailures();
        duplicateAndStorageFailures();
        hashingAndIdentityFailures();
        presentationIntegration();
        realHashingAndAssembly();
        System.out.println("Signup checks passed: " + checks);
    }

    private static SignupInputData valid() {
        return new SignupInputData(" alice ", " alice@example.test ", " secret ", " secret ");
    }

    private static void successfulRegistration() {
        MemoryDao dao = new MemoryDao();
        Result result = new Result();
        SignupInteractor interactor = new SignupInteractor(dao, result, p -> "hash:" + p, () -> "u1");
        interactor.execute(valid());
        User saved = dao.users.get("alice");
        check(result.successes == 1 && result.failures == 0, "one success");
        check(saved != null && dao.saves == 1, "saved exactly once");
        check(saved.getUserId().equals("u1"), "generated identity stored");
        check(saved.getUsername().equals("alice"), "username stripped");
        check(saved.getEmail().equals("alice@example.test"), "email stripped");
        check(saved.getPasswordHash().equals("hash: secret "), "password whitespace preserved for hashing");
        check(saved.getMcpkit() != null, "new account owns toolkit");
        check(result.output.getUserId().equals("u1"), "safe output identity");
        check(result.output.getUsername().equals("alice"), "safe output username");
        check(result.output.getEmail().equals("alice@example.test"), "safe output email");
    }

    private static void validationFailures() {
        SignupInputData[] invalid = {
            null,
            new SignupInputData(null, "a@example.test", "secret", "secret"),
            new SignupInputData("  ", "a@example.test", "secret", "secret"),
            new SignupInputData("alice", null, "secret", "secret"),
            new SignupInputData("alice", "alice", "secret", "secret"),
            new SignupInputData("alice", "a b@example.test", "secret", "secret"),
            new SignupInputData("alice", "a@@example.test", "secret", "secret"),
            new SignupInputData("alice", "a@example.test", null, null),
            new SignupInputData("alice", "a@example.test", "  ", "  "),
            new SignupInputData("alice", "a@example.test", "secret", null),
            new SignupInputData("alice", "a@example.test", "secret", "other")
        };
        for (SignupInputData input : invalid) {
            MemoryDao dao = new MemoryDao();
            Result result = new Result();
            new SignupInteractor(dao, result, p -> { throw new AssertionError("Must not hash invalid input"); })
                    .execute(input);
            check(result.failures == 1 && result.successes == 0, "validation failure reported");
            check(dao.lookups == 0 && dao.saves == 0, "invalid request does not touch DAO");
        }
    }

    private static void duplicateAndStorageFailures() {
        MemoryDao duplicate = new MemoryDao();
        User existing = new User("old", "alice", "old@example.test", "old-hash");
        duplicate.users.put("alice", existing);
        Result result = new Result();
        new SignupInteractor(duplicate, result, p -> { throw new AssertionError("Must not hash duplicate"); })
                .execute(valid());
        check(result.failures == 1 && duplicate.saves == 0, "duplicate rejected");
        check(duplicate.users.get("alice") == existing, "existing account unchanged");

        for (int failure = 1; failure <= 3; failure++) {
            MemoryDao dao = new MemoryDao();
            dao.failure = failure;
            Result output = new Result();
            new SignupInteractor(dao, output, p -> "encoded-hash", () -> "u1").execute(valid());
            check(output.failures == 1 && output.successes == 0, "lookup/save/race failure reported");
            check(dao.users.isEmpty(), "failed operation saves no user in test DAO");
            check(!output.error.contains("internal-sensitive-detail"), "internal exception not exposed");
        }
    }

    private static void hashingAndIdentityFailures() {
        MemoryDao dao = new MemoryDao();
        Result result = new Result();
        new SignupInteractor(dao, result, p -> { throw new IllegalStateException("secret"); })
                .execute(valid());
        check(result.failures == 1 && dao.saves == 0, "hash failure prevents save");
        check(!result.error.contains("secret"), "hash error sanitized");
        Result identityFailure = new Result();
        new SignupInteractor(dao, identityFailure, p -> "encoded-hash", () -> " ").execute(valid());
        check(identityFailure.failures == 1 && dao.saves == 0, "invalid generated ID prevents save");
    }

    private static void presentationIntegration() {
        MemoryDao dao = new MemoryDao();
        SignupViewModel model = new SignupViewModel();
        AtomicInteger events = new AtomicInteger();
        AtomicInteger navigation = new AtomicInteger();
        java.beans.PropertyChangeListener listener = e -> {
            check(e.getPropertyName().equals(SignupViewModel.RESULT_PROPERTY), "result event name");
            events.incrementAndGet();
        };
        model.addPropertyChangeListener(listener);
        SignupPresenter presenter = new SignupPresenter(model, navigation::incrementAndGet);
        SignupController controller = new SignupController(
                new SignupInteractor(dao, presenter, p -> "encoded-hash", () -> "u1"));
        controller.execute("alice", "alice@example.test", "secret", "secret");
        check(model.isSuccessful() && model.getUserId().equals("u1"), "controller-to-model success");
        check(model.getErrorMessage().isEmpty() && navigation.get() == 1, "success navigation once");
        controller.execute("alice", "alice@example.test", "secret", "secret");
        check(!model.isSuccessful() && !model.getErrorMessage().isEmpty(), "failure replaces success");
        check(model.getUserId().isEmpty() && model.getEmail().isEmpty(), "stale success cleared");
        controller.execute("alice", "alice@example.test", "secret", "secret");
        check(events.get() == 3 && navigation.get() == 1, "repeated failure notifies without navigation");
        model.removePropertyChangeListener(listener);
        presenter.prepareFailView("test");
        check(events.get() == 3, "listener can be removed");

        Result throwingPresenter = new Result() {
            @Override public void prepareSuccessView(SignupOutputData output) {
                throw new IllegalStateException("presentation failure");
            }
        };
        MemoryDao savedDao = new MemoryDao();
        try {
            new SignupInteractor(savedDao, throwingPresenter, p -> "hash", () -> "u2").execute(valid());
            throw new AssertionError("Expected presenter exception");
        } catch (IllegalStateException expected) {
            check(savedDao.users.size() == 1 && throwingPresenter.failures == 0,
                    "presentation error is not mislabeled as save failure");
        }
    }

    private static void realHashingAndAssembly() {
        Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher();
        String first = hasher.hash(" secret ");
        String second = hasher.hash(" secret ");
        check(!first.equals(second), "random salts produce distinct encodings");
        check(hasher.matches(" secret ", first), "correct password verifies");
        check(!hasher.matches("secret", first), "password whitespace matters");
        check(!hasher.matches("wrong", first), "wrong password rejected");
        check(!hasher.matches(null, first), "null password rejected");
        check(!hasher.matches("secret", "bad-format"), "malformed encoding rejected");
        check(!hasher.matches("secret", "pbkdf2-sha256$999999999$xx$xx"), "unbounded work factor rejected");
        MemoryDao dao = new MemoryDao();
        SignupViewModel model = new SignupViewModel();
        AtomicInteger navigation = new AtomicInteger();
        SignupController controller = SignupFactory.create(dao, model, navigation::incrementAndGet);
        controller.execute("bob", "bob@example.test", "password", "password");
        check(model.isSuccessful() && navigation.get() == 1, "production assembly runs registration");
        check(hasher.matches("password", dao.users.get("bob").getPasswordHash()), "saved hash usable by login");
        check(!dao.users.get("bob").getPasswordHash().equals("password"), "plaintext not saved");
    }

    private static class Result implements SignupOutputBoundary {
        int successes;
        int failures;
        String error;
        SignupOutputData output;
        @Override public void prepareSuccessView(SignupOutputData data) { successes++; output = data; }
        @Override public void prepareFailView(String message) { failures++; error = message; }
    }

    private static final class MemoryDao implements SignupDataAccessInterface {
        final Map<String, User> users = new HashMap<>();
        int lookups;
        int saves;
        int failure;
        @Override public boolean existsByUsername(String username) {
            lookups++;
            if (failure == 1) { throw new IllegalStateException("internal-sensitive-detail"); }
            return users.containsKey(username);
        }
        @Override public void save(User user) {
            saves++;
            if (failure == 2) { throw new IllegalStateException("internal-sensitive-detail"); }
            if (failure == 3 || users.containsKey(user.getUsername())) {
                throw new IllegalArgumentException("internal-sensitive-detail");
            }
            users.put(user.getUsername(), user);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
        checks++;
    }
}
