package csc207.mcp.frameworks_and_drivers.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import csc207.mcp.entity.ApiKey;
import csc207.mcp.frameworks_and_drivers.data_access_object.SqliteApiKeyDataAccessObject;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.json.JSONObject;

public final class ApiKeyDbHttpHandler implements HttpHandler {
    private final SqliteApiKeyDataAccessObject apiKeyDao;
    private final String internalSecret;

    public ApiKeyDbHttpHandler(SqliteApiKeyDataAccessObject apiKeyDao, String internalSecret) {
        this.apiKeyDao = Objects.requireNonNull(apiKeyDao, "apiKeyDao must not be null");
        this.internalSecret = DbHttpSupport.requireSecret(internalSecret);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DbHttpSupport.dispatch(exchange, internalSecret, (path, req) -> {
            switch (path) {
                case "/db/api-keys/save" -> {
                    ApiKey apiKey = new ApiKey(
                            req.getString("keyId"),
                            req.getString("ownerUserId"),
                            req.getString("keyHash"),
                            req.getString("displayPrefix"),
                            Instant.parse(req.getString("createdAt")),
                            parseNullableInstant(req, "lastUsedAt"),
                            parseNullableInstant(req, "revokedAt"));
                    apiKeyDao.save(apiKey);
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("ok", true));
                }
                case "/db/api-keys/find-by-id" -> {
                    Optional<ApiKey> found = apiKeyDao.findById(req.getString("keyId"));
                    JSONObject res = new JSONObject();
                    if (found.isEmpty()) {
                        res.put("apiKey", JSONObject.NULL);
                    } else {
                        res.put("apiKey", toJson(found.get()));
                    }
                    DbHttpSupport.sendJson(exchange, 200, res);
                }
                case "/db/api-keys/revoke" -> {
                    apiKeyDao.revoke(req.getString("keyId"), req.getString("ownerUserId"));
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("ok", true));
                }
                default -> DbHttpSupport.sendError(exchange, 404, "Unknown endpoint: " + path);
            }
        });
    }

    private static Instant parseNullableInstant(JSONObject obj, String key) {
        if (!obj.has(key) || obj.isNull(key)) {
            return null;
        }
        return Instant.parse(obj.getString(key));
    }

    private static JSONObject toJson(ApiKey apiKey) {
        JSONObject obj = new JSONObject();
        obj.put("keyId", apiKey.getKeyId());
        obj.put("ownerUserId", apiKey.getOwnerUserId());
        obj.put("keyHash", apiKey.getKeyHash());
        obj.put("displayPrefix", apiKey.getDisplayPrefix());
        obj.put("createdAt", apiKey.getCreatedAt().toString());
        obj.put("lastUsedAt", apiKey.getLastUsedAt() != null ? apiKey.getLastUsedAt().toString() : JSONObject.NULL);
        obj.put("revokedAt", apiKey.getRevokedAt() != null ? apiKey.getRevokedAt().toString() : JSONObject.NULL);
        return obj;
    }
}
