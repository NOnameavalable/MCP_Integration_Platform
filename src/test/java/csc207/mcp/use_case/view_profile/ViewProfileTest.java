package csc207.mcp.use_case.view_profile;

import csc207.mcp.app.ViewProfileFactory;
import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.User;
import csc207.mcp.interface_adapter.view_profile.ViewProfileViewModel;
import csc207.mcp.interface_adapter.create_api_key.CreateApiKeyController;
import csc207.mcp.interface_adapter.revoke_api_key.RevokeApiKeyController;
import csc207.mcp.use_case.create_api_key.CreateApiKeyInputData;
import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyInputData;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/** Standalone contract checks for DTO identity forwarding and safe profile presentation. */
public final class ViewProfileTest {
    private static int checks;
    private static final Instant NOW = Instant.parse("2026-11-21T00:00:00Z");
    public static void main(String[] args) {
        forwarding(); profile();
        System.out.println("Profile/DTO checks passed: " + checks);
    }
    private static void forwarding() {
        List<CreateApiKeyInputData> creates = new ArrayList<>();
        CreateApiKeyController create = new CreateApiKeyController(creates::add);
        create.execute("u1"); create.execute("u2");
        check(creates.get(0).getUserId().equals("u1") && creates.get(1).getUserId().equals("u2"),
                "each create request carries its current caller identity");
        List<RevokeApiKeyInputData> revokes = new ArrayList<>();
        new RevokeApiKeyController(revokes::add).execute("u2", "k1");
        check(revokes.get(0).getUserId().equals("u2") && revokes.get(0).getKeyId().equals("k1"),
                "revoke controller forwards both identifiers");
    }
    private static void profile() {
        Dao dao = new Dao(); Result result = new Result();
        ViewProfileInteractor useCase = new ViewProfileInteractor(dao, result);
        useCase.execute(new ViewProfileInputData("u1"));
        check(result.output.isSuccessful() && result.output.getUsername().equals("alice")
                && result.output.getEmail().equals("alice@example.test"), "profile account DTO");
        check(result.output.getKeys().size() == 1 && result.output.getKeys().get(0).isActive(), "safe key summary");
        check(dao.lastUserId.equals("u1") && dao.lastKeyOwner.equals("u1"), "input identity used for both lookups");
        for (var field : ViewProfileOutputData.ApiKeySummary.class.getDeclaredFields()) {
            check(!field.getName().toLowerCase().contains("hash") && !field.getName().toLowerCase().contains("raw"), "summary excludes secrets");
        }
        try { result.output.getKeys().clear(); throw new AssertionError("mutable summary list"); }
        catch (UnsupportedOperationException expected) { check(true, "immutable output list"); }
        List<ViewProfileOutputData.ApiKeySummary> source = new ArrayList<>();
        ViewProfileOutputData copy = new ViewProfileOutputData("a", "e", source); source.add(result.output.getKeys().get(0));
        check(copy.getKeys().isEmpty(), "defensive list copy");
        dao.keys = List.of(); useCase.execute(new ViewProfileInputData("u1"));
        check(result.output.isSuccessful() && result.output.getKeys().isEmpty(), "empty keys is a successful profile");
        for (ViewProfileInputData input : new ViewProfileInputData[]{null,
                new ViewProfileInputData(null), new ViewProfileInputData(" ")}) {
            int before = dao.lookups; useCase.execute(input);
            check(!result.output.isSuccessful() && dao.lookups == before, "invalid identity avoids DAO");
        }
        dao.user = Optional.empty(); useCase.execute(new ViewProfileInputData("u1"));
        check(!result.output.isSuccessful(), "missing account fails");
        dao.user = Optional.of(new User("other", "other", "other@example.test", "hash"));
        useCase.execute(new ViewProfileInputData("u1")); check(!result.output.isSuccessful(), "DAO wrong account rejected");
        dao.user = Optional.of(new User("u1", "alice", "alice@example.test", "password-hash"));
        dao.keys = List.of(new ApiKey("foreign", "other", "key-hash", "prefix", NOW));
        useCase.execute(new ViewProfileInputData("u1")); check(!result.output.isSuccessful(), "foreign key metadata rejected");
        for (int failure : new int[]{1, 2}) {
            dao.failure = failure; useCase.execute(new ViewProfileInputData("u1"));
            check(!result.output.isSuccessful() && !result.output.getErrorMessage().contains("sensitive"), "DAO errors sanitized");
        }
        dao.failure = 0; dao.keys = List.of();
        List<Runnable> queue = new ArrayList<>(); ViewProfileViewModel model = new ViewProfileViewModel();
        AtomicInteger events = new AtomicInteger(); model.stateProperty().addListener((o,a,b) -> events.incrementAndGet());
        var controller = ViewProfileFactory.create(dao, model, queue::add);
        controller.execute("u1");
        check(!model.getState().isSuccessful() && events.get() == 0, "profile success waits for UI dispatch");
        queue.remove(0).run(); check(model.getState().isSuccessful() && events.get() == 1, "profile controller/factory/presenter wiring");
        ViewProfileOutputData previous = model.getState();
        controller.execute(null); queue.remove(0).run();
        check(!model.getState().isSuccessful() && model.getState().getUsername().isEmpty()
                && model.getState().getEmail().isEmpty() && model.getState().getKeys().isEmpty(), "failure clears all previous profile data");
        check(previous.isSuccessful() && previous.getUsername().equals("alice"), "previous DTO remains immutable");
        controller.execute(null); queue.remove(0).run(); check(events.get() == 3, "repeated typed errors notify");
        check(!(model.stateProperty() instanceof javafx.beans.property.Property), "read-only profile property");
        Result throwing = new Result() {
            @Override public void prepareSuccessView(ViewProfileOutputData output) { throw new IllegalStateException("UI failure"); }
        };
        try {
            new ViewProfileInteractor(dao, throwing).execute(new ViewProfileInputData("u1"));
            throw new AssertionError("Expected presentation failure");
        } catch (IllegalStateException expected) { check(throwing.failures == 0, "UI failures are not mislabeled as storage failures"); }
    }
    private static class Result implements ViewProfileOutputBoundary {
        ViewProfileOutputData output; int failures;
        public void prepareSuccessView(ViewProfileOutputData output) { this.output = output; }
        public void prepareFailView(String errorMessage) {
            this.output = ViewProfileOutputData.failure(errorMessage); failures++;
        }
    }
    private static final class Dao implements ViewProfileDataAccessInterface {
        Optional<User> user = Optional.of(new User("u1", "alice", "alice@example.test", "password-hash"));
        List<ApiKey> keys = List.of(new ApiKey("k1", "u1", "key-hash", "mcp_prefix", NOW));
        String lastUserId, lastKeyOwner; int failure, lookups;
        public Optional<User> findUserById(String id) {
            lookups++; lastUserId = id;
            if (failure == 1) { throw new IllegalStateException("sensitive detail"); } return user;
        }
        public List<ApiKey> findApiKeysByUserId(String id) {
            lastKeyOwner = id;
            if (failure == 2) { throw new IllegalStateException("sensitive detail"); } return keys;
        }
    }
    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); } checks++;
    }
}
