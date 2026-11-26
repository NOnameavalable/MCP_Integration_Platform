package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.User;
import csc207.mcp.use_case.signup.*;
import java.nio.file.Files;
import java.nio.file.Path;

/** Integration checks using disposable SQLite databases, not account data. */
public final class UniqueEmailTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("unique-email-test");
        try {
            SqliteDatabaseManager database = new SqliteDatabaseManager(directory.resolve("new.db").toString());
            database.initializeTables();
            database.initializeTables();
            SqliteUserDataAccessObject dao = new SqliteUserDataAccessObject(database);
            dao.save(new User("one", "alice", "Alice@example.com", "hash"));
            check(dao.findByIdentifier("ALICE@example.com").getUserId().equals("one"), "Email login agrees with uniqueness");
            Result result = new Result();
            new SignupInteractor(dao, result, password -> "encoded-hash").execute(new SignupInputData("bob", "alice@example.com", "secret", "secret"));
            check(result.error.equals("This username or email is already registered."), "Use case reports duplicate email");
            check(dao.findByIdentifier("bob") == null, "Rejected signup creates no account");
            try {
                dao.save(new User("two", "bob", " alice@EXAMPLE.com ", "other-hash"));
                throw new AssertionError("Database must reject duplicate email even without precheck");
            } catch (IllegalArgumentException expected) {
                check(true, "Database rejects concurrent duplicate email");
            }
            check(dao.findByIdentifier("alice").getPasswordHash().equals("hash"), "Original account is preserved");
            SqliteDatabaseManager legacy = new SqliteDatabaseManager(directory.resolve("legacy.db").toString());
            try (var conn = legacy.getConnection(); var stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE users (user_id TEXT PRIMARY KEY, username TEXT UNIQUE, email TEXT, password_hash TEXT)");
                stmt.execute("INSERT INTO users VALUES ('1','a','same@example.com','h'),('2','b','SAME@example.com','h')");
            }
            try {
                legacy.initializeTables();
                throw new AssertionError("Legacy conflicting accounts require explicit resolution");
            } catch (RuntimeException expected) {
                try (var conn = legacy.getConnection(); var stmt = conn.createStatement();
                        var rows = stmt.executeQuery("SELECT count(*) FROM users")) {
                    check(rows.next() && rows.getInt(1) == 2, "Migration never deletes existing accounts");
                }
            }
            try (var conn = legacy.getConnection(); var stmt = conn.createStatement()) {
                stmt.execute("UPDATE users SET email = 'different@example.com' WHERE user_id = '2'");
            }
            legacy.initializeTables();
            SqliteUserDataAccessObject upgraded = new SqliteUserDataAccessObject(legacy);
            check(upgraded.findByIdentifier("SAME@example.com") != null, "Existing accounts survive successful upgrade");
            try {
                upgraded.save(new User("3", "c", "same@example.com", "hash"));
                throw new AssertionError("Existing databases must gain email uniqueness");
            } catch (IllegalArgumentException expected) {
                check(true, "Upgrade protects existing tables");
            }
            System.out.println("Unique email: " + checks + " checks passed");
        } finally {
            try (var files = Files.walk(directory)) {
                for (Path file : files.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
        checks++;
    }

    private static final class Result implements SignupOutputBoundary {
        String error;
        public void prepareSuccessView(SignupOutputData data) {
            throw new AssertionError("Duplicate email must not succeed");
        }
        public void prepareFailView(String message) { error = message; }
    }
}
