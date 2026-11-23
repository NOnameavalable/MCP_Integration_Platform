package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.use_case.create_api_key.CreateApiKeyDataAccessInterface;
import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyDataAccessInterface;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.json.JSONObject;

/** Client-side HTTP DAO for creating, querying, and revoking API keys. */
public final class HttpApiKeyDataAccessObject
        implements CreateApiKeyDataAccessInterface, RevokeApiKeyDataAccessInterface {
    private final HttpDbClient client;

    public HttpApiKeyDataAccessObject(HttpDbClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    @Override
    public void save(ApiKey apiKey) {
        JSONObject req = new JSONObject()
                .put("keyId", apiKey.getKeyId())
                .put("ownerUserId", apiKey.getOwnerUserId())
                .put("keyHash", apiKey.getKeyHash())
                .put("displayPrefix", apiKey.getDisplayPrefix())
                .put("createdAt", apiKey.getCreatedAt().toString())
                .put("lastUsedAt",
                        apiKey.getLastUsedAt() != null ? apiKey.getLastUsedAt().toString() : JSONObject.NULL)
                .put("revokedAt",
                        apiKey.getRevokedAt() != null ? apiKey.getRevokedAt().toString() : JSONObject.NULL);
        client.post("/db/api-keys/save", req);
    }

    @Override
    public Optional<ApiKey> findById(String keyId) {
        JSONObject req = new JSONObject().put("keyId", keyId);
        JSONObject res = client.post("/db/api-keys/find-by-id", req);
        if (res.isNull("apiKey")) {
            return Optional.empty();
        }
        JSONObject obj = res.getJSONObject("apiKey");
        return Optional.of(new ApiKey(
                obj.getString("keyId"),
                obj.getString("ownerUserId"),
                obj.getString("keyHash"),
                obj.getString("displayPrefix"),
                Instant.parse(obj.getString("createdAt")),
                parseNullableInstant(obj, "lastUsedAt"),
                parseNullableInstant(obj, "revokedAt")));
    }

    @Override
    public void revoke(String keyId, String ownerUserId) {
        JSONObject req = new JSONObject()
                .put("keyId", keyId)
                .put("ownerUserId", ownerUserId);
        client.post("/db/api-keys/revoke", req);
    }

    private static Instant parseNullableInstant(JSONObject obj, String key) {
        if (!obj.has(key) || obj.isNull(key)) {
            return null;
        }
        return Instant.parse(obj.getString(key));
    }
}
