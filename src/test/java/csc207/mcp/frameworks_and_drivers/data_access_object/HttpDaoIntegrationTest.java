package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.User;
import csc207.mcp.frameworks_and_drivers.gateway.McpGatewayServer;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitDataAccessInterface;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitInputData;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitInteractor;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputBoundary;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitOutputData;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

public final class HttpDaoIntegrationTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        Path tempDir = Files.createTempDirectory("http-dao-integration-test");
        McpGatewayServer server = null;
        try {
            SqliteDatabaseManager db = new SqliteDatabaseManager(tempDir.resolve("test.db").toString());
            db.initializeTables();

            String secret = "super-secret-internal-key";
            server = new McpGatewayServer(null, 0, 16, db, secret);
            server.start();
            String baseUrl = "http://127.0.0.1:" + server.getPort();

            // 1. Secret enforcement
            HttpDbClient badClient = new HttpDbClient(baseUrl, "wrong-secret");
            HttpUserDataAccessObject badUserDao = new HttpUserDataAccessObject(badClient);
            try {
                badUserDao.existsByUsername("alice");
                throw new AssertionError("Wrong secret must be rejected");
            } catch (IllegalStateException expected) {
                check(true, "Rejects wrong X-Internal-Secret");
            }

            // 2. User DAO over HTTP
            HttpDbClient client = new HttpDbClient(baseUrl, secret);
            HttpUserDataAccessObject userDao = new HttpUserDataAccessObject(client);
            check(!userDao.existsByUsername("alice"), "Unknown username does not exist");
            check(!userDao.existsByEmail("alice@example.com"), "Unknown email does not exist");
            check(userDao.findByIdentifier("alice") == null, "Unknown identifier returns null");

            userDao.save(new User("u1", "alice", "Alice@Example.com", "pw-hash-1"));
            check(userDao.existsByUsername("alice"), "Saved username exists");
            check(userDao.existsByEmail(" alice@example.com "), "Saved email exists (case/space normalized)");

            User foundByName = userDao.findByIdentifier("alice");
            check(foundByName != null && "u1".equals(foundByName.getUserId()), "Find by username works");
            User foundByEmail = userDao.findByIdentifier("ALICE@example.com");
            check(foundByEmail != null && "pw-hash-1".equals(foundByEmail.getPasswordHash()),
                    "Find by email works");

            try {
                userDao.save(new User("u2", "bob", "alice@example.com", "pw-hash-2"));
                throw new AssertionError("Duplicate email must throw IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
                check(true, "Duplicate email maps 409 to IllegalArgumentException");
            }

            // 3. ApiKey DAO over HTTP
            HttpApiKeyDataAccessObject apiKeyDao = new HttpApiKeyDataAccessObject(client);
            Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
            apiKeyDao.save(new ApiKey("k1", "u1", "key-hash-1", "mcp_1234", createdAt));

            Optional<ApiKey> loadedKey = apiKeyDao.findById("k1");
            check(loadedKey.isPresent()
                    && "u1".equals(loadedKey.get().getOwnerUserId())
                    && loadedKey.get().isActive(),
                    "Saved API key is loaded properly");
            check(apiKeyDao.findById("missing").isEmpty(), "Missing API key returns Optional.empty");

            try {
                apiKeyDao.revoke("k1", "wrong-owner");
                throw new AssertionError("Revoking another user's key must throw IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
                check(true, "Revoke with wrong owner maps to IllegalArgumentException");
            }

            apiKeyDao.revoke("k1", "u1");
            Optional<ApiKey> revokedKey = apiKeyDao.findById("k1");
            check(revokedKey.isPresent() && !revokedKey.get().isActive(), "Revoked API key is marked inactive");

            // Malformed timestamp fields on /db/api-keys/save must return 400 (not 500)
            HttpClient rawHttp = HttpClient.newHttpClient();
            assertMalformedApiKeyTimestampReturns400(rawHttp, baseUrl, secret, "createdAt",
                    new JSONObject()
                            .put("keyId", "k-bad-created")
                            .put("ownerUserId", "u1")
                            .put("keyHash", "key-hash-bad")
                            .put("displayPrefix", "mcp_bad1")
                            .put("createdAt", "not-a-date")
                            .put("lastUsedAt", JSONObject.NULL)
                            .put("revokedAt", JSONObject.NULL));
            assertMalformedApiKeyTimestampReturns400(rawHttp, baseUrl, secret, "lastUsedAt",
                    new JSONObject()
                            .put("keyId", "k-bad-last-used")
                            .put("ownerUserId", "u1")
                            .put("keyHash", "key-hash-bad")
                            .put("displayPrefix", "mcp_bad2")
                            .put("createdAt", "2026-01-01T00:00:00Z")
                            .put("lastUsedAt", "not-a-date")
                            .put("revokedAt", JSONObject.NULL));
            assertMalformedApiKeyTimestampReturns400(rawHttp, baseUrl, secret, "revokedAt",
                    new JSONObject()
                            .put("keyId", "k-bad-revoked")
                            .put("ownerUserId", "u1")
                            .put("keyHash", "key-hash-bad")
                            .put("displayPrefix", "mcp_bad3")
                            .put("createdAt", "2026-01-01T00:00:00Z")
                            .put("lastUsedAt", JSONObject.NULL)
                            .put("revokedAt", "not-a-date"));

            // 4. Mcpkit DAO over HTTP
            try (var conn = db.getConnection(); var stmt = conn.createStatement()) {
                stmt.execute("INSERT INTO mcp_servers (mcp_id, name, description) "
                        + "VALUES ('m1', 'weather', 'Weather MCP')");
                stmt.execute("INSERT INTO tools (mcp_id, name, description, parameters_json) "
                        + "VALUES ('m1', 'get_forecast', 'Get forecast', '[\"city\",\"days\"]')");
                stmt.execute("INSERT INTO user_mcpkit (user_id, mcp_id) VALUES ('u1', 'm1')");
            }

            HttpMcpkitDataAccessObject mcpkitDao = new HttpMcpkitDataAccessObject(client);
            check(mcpkitDao.userExists("u1"), "Existing user returns true");
            check(!mcpkitDao.userExists("no-such-user"), "Non-existing user returns false");

            McpServer mcp = mcpkitDao.getMcpById("m1");
            check(mcp != null
                    && "weather".equals(mcp.getMcpName())
                    && mcp.getTools().size() == 1
                    && List.of("city", "days").equals(mcp.getTools().get(0).getParameters()),
                    "getMcpById deserializes server and tools");
            check(mcpkitDao.getMcpById("missing-mcp") == null, "Missing MCP returns null");

            List<McpServer> kit = mcpkitDao.getMcpkitByUserId("u1");
            check(kit.size() == 1 && "m1".equals(kit.get(0).getMcpId()), "getMcpkitByUserId returns user's kit");

            mcpkitDao.removeFromUserMcpkit("u1", "m1");
            check(mcpkitDao.getMcpkitByUserId("u1").isEmpty(), "removeFromUserMcpkit removes entry");

            addToToolkitOverHttp(db, client);

            System.out.println("HTTP DAO integration: " + checks + " checks passed");
        } finally {
            if (server != null) {
                server.stop();
            }
            try (var files = Files.walk(tempDir)) {
                for (Path file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private static void addToToolkitOverHttp(SqliteDatabaseManager db, HttpDbClient client) throws Exception {
        try (var conn = db.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO mcp_servers VALUES ('m2', 'search', 'Search MCP'), "
                    + "('m3', 'other', 'Other MCP')");
        }
        AddToToolkitDataAccessInterface dao = new HttpMcpkitDataAccessObject(client);
        check(dao.getUserById("missing") == null, "HTTP toolkit lookup returns null for missing user");
        User user = dao.getUserById("u1");
        check(user.getUsername().equals("alice") && user.getEmail().equals("Alice@Example.com")
                && user.getPasswordHash().equals("pw-hash-1"), "HTTP toolkit lookup restores account fields");
        check(user.getMcpkit().userIdGetter().equals("u1") && user.getMcpkit().mcpkitGetter().isEmpty(),
                "HTTP toolkit lookup restores an empty owned toolkit");
        user.getMcpkit().addTool(dao.getMcpById("m1"));
        dao.saveUser(user);
        dao.saveUser(user);
        User restored = dao.getUserById("u1");
        check(restored.getMcpkit().mcpkitGetter().size() == 1, "HTTP save avoids duplicate memberships");
        McpServer weather = restored.getMcpkit().mcpkitGetter().get(0);
        check(weather.getMcpId().equals("m1") && weather.getTools().size() == 1
                && weather.getTools().get(0).getParameters().equals(List.of("city", "days")),
                "HTTP user lookup includes MCP tools and parameters");

        int[] successes = {0};
        int[] failures = {0};
        AddToToolkitOutputBoundary presenter = new AddToToolkitOutputBoundary() {
            public void prepareSuccessView(AddToToolkitOutputData data) {
                successes[0]++;
                check(dao.getUserById(data.getUserId()).getMcpkit().contains(dao.getMcpById(data.getServerId())),
                        "Interactor presents success after HTTP save commits");
            }

            public void prepareFailView(String message) {
                failures[0]++;
            }
        };
        AddToToolkitInteractor interactor = new AddToToolkitInteractor(dao, presenter);
        interactor.excute(new AddToToolkitInputData("u1", "m2"));
        interactor.excute(new AddToToolkitInputData("u1", "m2"));
        check(successes[0] == 1 && failures[0] == 1, "Interactor adds through HTTP and rejects duplicates");
        dao.saveUser(user);
        check(dao.getUserById("u1").getMcpkit().mcpkitGetter().size() == 2,
                "HTTP save of stale toolkit preserves other entries");

        dao.saveUser(new User("u1", "ignored", "ignored@example.com", "ignored-hash"));
        check(dao.getUserById("u1").getUsername().equals("alice")
                && dao.getUserById("u1").getPasswordHash().equals("pw-hash-1"),
                "HTTP toolkit save preserves account fields");
        User invalid = dao.getUserById("u1");
        invalid.getMcpkit().addTool(dao.getMcpById("m3"));
        invalid.getMcpkit().addTool(new McpServer("Missing", "", "missing"));
        try {
            dao.saveUser(invalid);
            throw new AssertionError("Unknown MCP must be rejected over HTTP");
        } catch (IllegalArgumentException expected) {
            check(dao.getUserById("u1").getMcpkit().mcpkitGetter().size() == 2,
                    "HTTP save failure leaves persisted toolkit intact");
        }
        try {
            dao.saveUser(new User("missing", "missing", "missing@example.com", "hash"));
            throw new AssertionError("Unknown user must be rejected over HTTP");
        } catch (IllegalArgumentException expected) {
            check(dao.getUserById("missing") == null, "HTTP save does not register a missing user");
        }
    }

    private static void assertMalformedApiKeyTimestampReturns400(
            HttpClient rawHttp, String baseUrl, String secret, String fieldName, JSONObject payload)
            throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/db/api-keys/save"))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("X-Internal-Secret", secret)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = rawHttp.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        JSONObject body = new JSONObject(res.body());
        check(res.statusCode() == 400 && body.has("error"),
                "Malformed " + fieldName + " returns 400 with error payload");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }
}
