package csc207.mcp.use_case.create_api_key;

import csc207.mcp.entity.ApiKey;

/** Data operation required to persist a newly created API key. */
public interface CreateApiKeyDataAccessInterface {

    /**
     * Saves a new API key record, including its owner, hash, and metadata.
     * The record must not contain the full secret. Existing records must
     * not be overwritten if an identifier or key hash conflicts.
     *
     * @param apiKey the new API key record to persist
     * @throws IllegalArgumentException if the record conflicts with an existing key
     * @throws IllegalStateException if the record cannot be saved
     */
    void save(ApiKey apiKey);
}
