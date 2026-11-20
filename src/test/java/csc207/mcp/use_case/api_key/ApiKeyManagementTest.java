package csc207.mcp.use_case.api_key;

import csc207.mcp.app.CreateApiKeyFactory;
import csc207.mcp.app.RevokeApiKeyFactory;
import csc207.mcp.entity.ApiKey;
import csc207.mcp.frameworks_and_drivers.security.SecureApiKeyCredentials;
import csc207.mcp.interface_adapter.create_api_key.*;
import csc207.mcp.interface_adapter.revoke_api_key.*;
import csc207.mcp.use_case.create_api_key.*;
import csc207.mcp.use_case.revoke_api_key.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Standalone checks: no database, network, or JavaFX toolkit startup required. */
public final class ApiKeyManagementTest {
    private static int checks;
    private static final Instant NOW = Instant.parse("2026-11-19T00:00:00Z");
    private static final String SECRET = "mcp_abcdefghijklmnopqrstuvwxyz0123456789ABCDEFG";
    private static final ApiKeyCredentials CREDENTIALS = new ApiKeyCredentials() {
        public String generateSecret() { return SECRET; }
        public String hashSecret(String secret) { return "encoded-hash"; }
    };
    public static void main(String[] args) {
        creation(); revocation(); presentation(); credentials();
        System.out.println("API key management checks passed: " + checks);
    }
    private static CreateApiKeyInteractor creator(Dao dao, Created result,
            ApiKeyCredentials credentials) {
        return new CreateApiKeyInteractor(dao, result, credentials,
                Clock.fixed(NOW, ZoneOffset.UTC), () -> "k1");
    }
    private static void creation() {
        Dao dao = new Dao(); Created result = new Created();
        creator(dao, result, CREDENTIALS).execute(new CreateApiKeyInputData("u1"));
        check(result.successes == 1 && result.failures == 0, "create success");
        ApiKey key = dao.keys.get("k1");
        check(key.belongsTo("u1") && key.isActive(), "session ownership and active key");
        check(key.getCreatedAt().equals(NOW), "injected clock");
        check(key.getKeyHash().equals("encoded-hash") && !key.getKeyHash().equals(SECRET), "persist hash only");
        check(result.output.getRawKey().equals(SECRET), "one-time secret result");
        check(result.output.getDisplayPrefix().equals(SECRET.substring(0, 12)), "safe prefix");
        for (String user : new String[]{null, "", " "}) {
            Dao rejected = new Dao(); Created error = new Created();
            creator(rejected, error, CREDENTIALS).execute(new CreateApiKeyInputData(user));
            check(error.failures == 1 && rejected.saves == 0, "unauthenticated request rejected");
        }
        Created absent = new Created(); Dao untouched = new Dao();
        creator(untouched, absent, CREDENTIALS).execute(null);
        check(absent.failures == 1 && untouched.saves == 0, "null request rejected");
        for (int failure : new int[]{1, 2}) {
            Dao failed = new Dao(); failed.failure = failure; Created error = new Created();
            creator(failed, error, CREDENTIALS).execute(new CreateApiKeyInputData("u1"));
            check(error.successes == 0 && error.output == null && error.failures == 1, "failed save never reveals secret");
            check(failed.keys.isEmpty() && !error.error.contains(SECRET), "failed save leaves no record in fake DAO");
        }
        for (String bad : new String[]{null, "short", " " .repeat(40)}) {
            Created error = new Created(); Dao rejected = new Dao();
            creator(rejected, error, new ApiKeyCredentials() {
                public String generateSecret() { return bad; }
                public String hashSecret(String s) { throw new AssertionError("must not hash invalid secret"); }
            }).execute(new CreateApiKeyInputData("u1"));
            check(error.failures == 1 && rejected.saves == 0, "bad generator output rejected");
        }
        Created badHash = new Created();
        creator(new Dao(), badHash, new ApiKeyCredentials() {
            public String generateSecret() { return SECRET; }
            public String hashSecret(String s) { return s; }
        }).execute(new CreateApiKeyInputData("u1"));
        check(badHash.failures == 1, "plaintext hash rejected");
        Dao saved = new Dao();
        try {
            creator(saved, new Created() {
                public void prepareSuccessView(CreateApiKeyOutputData out) { throw new IllegalStateException("UI"); }
            }, CREDENTIALS).execute(new CreateApiKeyInputData("u1"));
            throw new AssertionError("expected presenter failure");
        } catch (IllegalStateException expected) { check(saved.keys.size() == 1, "presentation failure does not undo saved credential"); }
    }
    private static void revocation() {
        Dao dao = new Dao(); dao.keys.put("k1", key("u1")); Revoked result = new Revoked();
        RevokeApiKeyInteractor revoke = new RevokeApiKeyInteractor(dao, result);
        revoke.execute(new RevokeApiKeyInputData("u1", "k1"));
        check(result.successes == 1 && !result.output.isAlreadyRevoked(), "first revocation confirmed");
        check(!dao.keys.get("k1").isActive() && dao.revokes == 1, "DAO persisted revocation");
        revoke.execute(new RevokeApiKeyInputData("u1", "k1"));
        check(result.successes == 2 && result.output.isAlreadyRevoked() && dao.revokes == 1, "idempotent repeated revocation");
        for (String owner : new String[]{"other", null, " "}) {
            Dao denied = new Dao(); denied.keys.put("k1", key("u1")); Revoked error = new Revoked();
            new RevokeApiKeyInteractor(denied, error).execute(new RevokeApiKeyInputData(owner, "k1"));
            check(error.failures == 1 && denied.revokes == 0 && denied.keys.get("k1").isActive(), "unauthorized owner rejected");
        }
        for (RevokeApiKeyInputData input : new RevokeApiKeyInputData[]{null,
                new RevokeApiKeyInputData("u1", null), new RevokeApiKeyInputData("u1", " ")}) {
            Dao denied = new Dao(); Revoked error = new Revoked();
            new RevokeApiKeyInteractor(denied, error).execute(input);
            check(error.failures == 1 && denied.lookups == 0, "invalid selection avoids DAO");
        }
        Revoked missing = new Revoked();
        new RevokeApiKeyInteractor(new Dao(), missing).execute(new RevokeApiKeyInputData("u1", "missing"));
        check(missing.failures == 1, "missing key rejected");
        for (int failure : new int[]{1, 2, 3}) {
            Dao failed = new Dao(); failed.keys.put("k1", key("u1")); failed.failure = failure; Revoked error = new Revoked();
            new RevokeApiKeyInteractor(failed, error).execute(new RevokeApiKeyInputData("u1", "k1"));
            check(error.failures == 1 && error.successes == 0, "lookup/update/race error reported");
            check(failed.keys.get("k1").isActive() && !error.error.contains("sensitive"), "no premature local mutation or internal details");
        }
    }
    private static void presentation() {
        List<Runnable> queue = new ArrayList<>(); AtomicInteger displayed = new AtomicInteger();
        CreateApiKeyViewModel created = new CreateApiKeyViewModel(); AtomicInteger events = new AtomicInteger();
        created.stateProperty().addListener((o,a,b) -> events.incrementAndGet());
        CreateApiKeyController controller = CreateApiKeyFactory.create(new Dao(), created, out -> {
            check(created.getState().isSuccessful(), "state precedes secret display");
            check(out.getRawKey().startsWith("mcp_"), "one-time display receives credential"); displayed.incrementAndGet();
        }, queue::add);
        controller.execute("u1");
        check(!created.getState().isSuccessful() && displayed.get() == 0 && queue.size() == 1, "creation waits for UI dispatch");
        queue.remove(0).run();
        check(displayed.get() == 1 && events.get() == 1, "one confirmed creation presentation");
        CreateApiKeyViewModel.Result prior = created.getState();
        CreateApiKeyPresenter presenter = new CreateApiKeyPresenter(created, out -> { throw new AssertionError(); }, queue::add);
        presenter.prepareFailView("failed"); queue.remove(0).run();
        presenter.prepareFailView("failed"); queue.remove(0).run();
        check(events.get() == 3 && prior.isSuccessful(), "repeated errors notify; previous snapshot immutable");
        check(created.getState().getKeyId().isEmpty() && !created.getState().isSuccessful(), "failure clears stale key metadata");
        check(!(created.stateProperty() instanceof javafx.beans.property.Property), "read-only creation state");
        for (var field : CreateApiKeyViewModel.Result.class.getDeclaredFields()) {
            check(!field.getName().toLowerCase().contains("raw") && !field.getName().toLowerCase().contains("hash"), "no credentials in observable state");
        }
        Dao dao = new Dao(); dao.keys.put("k1", key("u1")); RevokeApiKeyViewModel revoked = new RevokeApiKeyViewModel();
        AtomicInteger refresh = new AtomicInteger();
        RevokeApiKeyController revoke = RevokeApiKeyFactory.create(dao, revoked, () -> {
            check(revoked.getState().isSuccessful(), "state precedes profile refresh"); refresh.incrementAndGet();
        }, queue::add);
        revoke.execute("u1", "k1");
        check(refresh.get() == 0 && queue.size() == 1, "revocation callback queued"); queue.remove(0).run();
        check(refresh.get() == 1 && revoked.getState().getKeyId().equals("k1"), "revoke controller/factory/model integration");
        revoke.execute("u1", "missing"); queue.remove(0).run();
        check(!revoked.getState().isSuccessful() && revoked.getState().getKeyId().isEmpty() && refresh.get() == 1, "failed revoke clears state without refresh");
        check(!(revoked.stateProperty() instanceof javafx.beans.property.Property), "read-only revocation state");
    }
    private static void credentials() {
        SecureApiKeyCredentials credentials = new SecureApiKeyCredentials();
        String first = credentials.generateSecret(), second = credentials.generateSecret();
        check(first.matches("mcp_[A-Za-z0-9_-]{43}") && !first.equals(second), "256 random bits and URL-safe encoding");
        String hash = credentials.hashSecret(first);
        check(hash.startsWith("sha256$") && !hash.contains(first), "encoded verification hash");
        check(credentials.matches(first, hash), "gateway verification");
        check(!credentials.matches(second, hash) && !credentials.matches(null, hash)
                && !credentials.matches(first, null), "wrong/missing credentials rejected");
    }
    private static ApiKey key(String owner) { return new ApiKey("k1", owner, "hash", "mcp_prefix", NOW); }
    private static class Created implements CreateApiKeyOutputBoundary {
        int successes, failures; String error; CreateApiKeyOutputData output;
        public void prepareSuccessView(CreateApiKeyOutputData output) { successes++; this.output = output; }
        public void prepareFailView(String errorMessage) { failures++; error = errorMessage; }
    }
    private static final class Revoked implements RevokeApiKeyOutputBoundary {
        int successes, failures; String error; RevokeApiKeyOutputData output;
        public void prepareSuccessView(RevokeApiKeyOutputData output) { successes++; this.output = output; }
        public void prepareFailView(String errorMessage) { failures++; error = errorMessage; }
    }
    private static final class Dao implements CreateApiKeyDataAccessInterface, RevokeApiKeyDataAccessInterface {
        final Map<String,ApiKey> keys = new HashMap<>(); int failure, saves, lookups, revokes;
        public void save(ApiKey key) {
            saves++;
            if (failure == 1) { throw new IllegalArgumentException("sensitive"); }
            if (failure == 2) { throw new IllegalStateException("sensitive"); }
            if (keys.putIfAbsent(key.getKeyId(), key) != null) { throw new IllegalArgumentException("conflict"); }
        }
        public Optional<ApiKey> findById(String id) {
            lookups++; if (failure == 1) { throw new IllegalStateException("sensitive"); }
            return Optional.ofNullable(keys.get(id));
        }
        public void revoke(String id, String owner) {
            revokes++;
            if (failure == 2) { throw new IllegalStateException("sensitive"); }
            if (failure == 3 || !keys.containsKey(id) || !keys.get(id).belongsTo(owner)) {
                throw new IllegalArgumentException("sensitive");
            }
            keys.get(id).revoke(NOW.plusSeconds(1));
        }
    }
    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); } checks++;
    }
}
