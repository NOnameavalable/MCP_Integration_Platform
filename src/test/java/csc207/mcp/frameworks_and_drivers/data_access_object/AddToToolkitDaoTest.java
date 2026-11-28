package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.Mcpkit;
import csc207.mcp.entity.User;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitDataAccessInterface;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitInputData;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitInteractor;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputBoundary;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputData;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/** Integration checks against a disposable SQLite database. Run through main(). */
public final class AddToToolkitDaoTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("add-to-toolkit-dao-test");
        try {
            SqliteDatabaseManager database = new SqliteDatabaseManager(directory.resolve("test.db").toString());
            database.initializeTables();
            SqliteUserDataAccessObject users = new SqliteUserDataAccessObject(database);
            users.save(new User("u1", "alice", "alice@example.com", "hash"));
            users.save(new User("u2", "bob", "bob@example.com", "other-hash"));
            try (var conn = database.getConnection(); var stmt = conn.createStatement()) {
                stmt.execute("INSERT INTO mcp_servers VALUES "
                        + "('m1', 'Weather', 'Weather MCP'), ('m2', 'Search', 'Search MCP'), "
                        + "('m3', 'Other', 'Other MCP')");
                stmt.execute("INSERT INTO tools VALUES ('m1', 'forecast', 'Get forecast', '[\"city\"]')");
            }

            AddToToolkitDataAccessInterface dao = new SqliteMcpkitDataAccessObject(database);
            check(dao.getUserById("missing") == null, "missing user returns null");
            check(dao.getMcpById("missing") == null, "missing MCP returns null");
            User initial = dao.getUserById("u1");
            check(initial.getMcpkit().mcpkitGetter().isEmpty(), "new user's toolkit is empty");
            dao.saveUser(initial);
            check(dao.getUserById("u1") != null, "saving an existing empty toolkit succeeds");

            initial.getMcpkit().addTool(dao.getMcpById("m1"));
            dao.saveUser(initial);
            dao.saveUser(initial);
            User restored = dao.getUserById("u1");
            check(restored.getUsername().equals("alice") && restored.getEmail().equals("alice@example.com")
                    && restored.getPasswordHash().equals("hash"), "restores account fields");
            check(restored.getMcpkit().userIdGetter().equals("u1")
                    && restored.getMcpkit().mcpkitGetter().size() == 1, "restores owned toolkit without duplicates");
            McpServer weather = restored.getMcpkit().mcpkitGetter().get(0);
            check(weather.getMcpId().equals("m1") && weather.getDescription().equals("Weather MCP")
                    && weather.getTools().get(0).getName().equals("forecast")
                    && weather.getTools().get(0).getParameters().equals(List.of("city")),
                    "restores MCP metadata, tools, and parameters");

            Result result = new Result(dao);
            AddToToolkitInteractor interactor = new AddToToolkitInteractor(dao, result);
            interactor.excute(new AddToToolkitInputData("u1", "m2"));
            interactor.excute(new AddToToolkitInputData("u1", "m2"));
            check(result.successes == 1 && result.failures == 1, "real DAO supports addition and duplicate rejection");

            // Saving an older snapshot must preserve an entry added since it was loaded.
            dao.saveUser(initial);
            check(dao.getUserById("u1").getMcpkit().mcpkitGetter().size() == 2,
                    "stale saves preserve other persisted entries");
            check(dao.getUserById("u2").getMcpkit().mcpkitGetter().isEmpty(), "other user's toolkit is unchanged");
            dao.saveUser(new User("u1", "changed", "changed@example.com", "changed-hash"));
            check(users.findByIdentifier("alice").getPasswordHash().equals("hash"),
                    "toolkit save does not overwrite account fields");

            User invalid = dao.getUserById("u1");
            invalid.getMcpkit().addTool(dao.getMcpById("m3"));
            invalid.getMcpkit().addTool(new McpServer("Missing", "", "missing"));
            try {
                dao.saveUser(invalid);
                throw new AssertionError("Missing catalog MCP must fail");
            } catch (IllegalStateException expected) {
                check(dao.getUserById("u1").getMcpkit().mcpkitGetter().size() == 2,
                        "failed batch rolls back its earlier inserts");
            }

            try {
                dao.saveUser(new User("missing", "missing", "missing@example.com", "hash"));
                throw new AssertionError("Missing user must fail even with an empty toolkit");
            } catch (IllegalArgumentException expected) {
                check(dao.getUserById("missing") == null, "save does not create a missing user");
            }
            try {
                dao.saveUser(new User("u1", "alice", "alice@example.com", "hash", new Mcpkit("u2")));
                throw new AssertionError("Mismatched toolkit owner must fail");
            } catch (IllegalArgumentException expected) {
                check(dao.getUserById("u2").getMcpkit().mcpkitGetter().isEmpty(), "mismatched owner cannot write");
            }
            System.out.println("Add-to-toolkit DAO checks passed: " + checks);
        } finally {
            try (var files = Files.walk(directory)) {
                for (Path file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private static final class Result implements AddToToolkitOutputBoundary {
        private final AddToToolkitDataAccessInterface dao;
        private int successes;
        private int failures;

        private Result(AddToToolkitDataAccessInterface dao) {
            this.dao = dao;
        }

        public void prepareSuccessView(AddToToolkitOutputData data) {
            successes++;
            check(dao.getUserById(data.getUserId()).getMcpkit().contains(dao.getMcpById(data.getServerId())),
                    "success is presented after transaction commit");
        }

        public void prepareFailView(String message) {
            failures++;
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
        checks++;
    }
}
