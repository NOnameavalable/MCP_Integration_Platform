package csc207.mcp.use_case.revoke_api_key;

import csc207.mcp.entity.ApiKey;
import java.util.Optional;

/** Data operations required to inspect and revoke an API key. */
public interface RevokeApiKeyDataAccessInterface {

    /**
     * Loads the selected key record for ownership and status checks.
     *
     * @param keyId the non-secret key record identifier, not the full API key
     * @return the key record, or empty if no such key exists
     * @throws IllegalStateException if storage cannot be queried
     */
    Optional<ApiKey> findById(String keyId);

    /**
     * Persists revocation only if the key belongs to the specified user.
     * Ownership must be checked again as part of the update, so an earlier
     * lookup alone is not sufficient. Already revoked keys remain revoked.
     * This operation does not delete the key record or log the user out.
     *
     * @param keyId the non-secret identifier of the key to revoke
     * @param ownerUserId the identity obtained from authenticated user context
     * @throws IllegalArgumentException if the key is missing or belongs to another user
     * @throws IllegalStateException if revocation cannot be persisted
     */
    void revoke(String keyId, String ownerUserId);
}
