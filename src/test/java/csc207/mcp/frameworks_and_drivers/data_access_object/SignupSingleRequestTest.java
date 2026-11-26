package csc207.mcp.frameworks_and_drivers.data_access_object;

import com.sun.net.httpserver.HttpServer;
import csc207.mcp.frameworks_and_drivers.gateway.UserDbHttpHandler;
import csc207.mcp.use_case.signup.SignupInputData;
import csc207.mcp.use_case.signup.SignupInteractor;
import csc207.mcp.use_case.signup.SignupOutputBoundary;
import csc207.mcp.use_case.signup.SignupOutputData;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/** Exercises signup through real HTTP and SQLite, counting network requests. */
public final class SignupSingleRequestTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        var directory = Files.createTempDirectory("signup-single-request");
        HttpServer server = null;
        try {
            var database = new SqliteDatabaseManager(directory.resolve("users.db").toString());
            database.initializeTables();
            var sqlite = new SqliteUserDataAccessObject(database);
            var requests = new AtomicInteger();
            var handler = new UserDbHttpHandler(sqlite, "test-secret");
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/db/users", exchange -> {
                requests.incrementAndGet();
                handler.handle(exchange);
            });
            server.start();
            var http = new HttpUserDataAccessObject(new HttpDbClient(
                    "http://127.0.0.1:" + server.getAddress().getPort(), "test-secret"));
            Result result = new Result();
            AtomicInteger identifiers = new AtomicInteger();
            var signup = new SignupInteractor(http, result, password -> "hash:" + password,
                    () -> "user-" + identifiers.incrementAndGet());
            signup.execute(new SignupInputData("alice", "Alice@example.com", "secret", "secret"));
            check(requests.get() == 1, "Successful signup sends exactly one HTTP request");
            check(result.successes == 1 && result.failures == 0, "Saved account reports success");
            check(sqlite.findByIdentifier("alice") != null, "Account was persisted");

            signup.execute(new SignupInputData("alice", "other@example.com", "other", "other"));
            check(requests.get() == 2, "Duplicate username sends one save request");
            check(result.failures == 1 && result.successes == 1, "409 username conflict reports failure");
            signup.execute(new SignupInputData("bob", " alice@EXAMPLE.com ", "other", "other"));
            check(requests.get() == 3, "Duplicate email sends one save request");
            check(result.failures == 2 && result.successes == 1, "409 email conflict reports failure");
            check(result.error.equals("This username or email is already registered."),
                    "Duplicate-account message is presented");
            check(sqlite.findByIdentifier("alice").getPasswordHash().equals("hash:secret"),
                    "Duplicate attempts preserve the original credentials");
            check(sqlite.findByIdentifier("bob") == null, "Duplicate email creates no second account");
            signup.execute(new SignupInputData("carol", "carol@example.com", "secret", "different"));
            check(requests.get() == 3, "Invalid local input sends no HTTP request");
            check(result.failures == 3, "Invalid local input reports failure");
            System.out.println("Single-request signup checks passed: " + checks);
        } finally {
            if (server != null) { server.stop(0); }
            try (var files = Files.walk(directory)) {
                for (var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) { throw new AssertionError(description); }
        checks++;
    }

    private static final class Result implements SignupOutputBoundary {
        int successes;
        int failures;
        String error;
        @Override public void prepareSuccessView(SignupOutputData data) { successes++; }
        @Override public void prepareFailView(String message) { failures++; error = message; }
    }
}
