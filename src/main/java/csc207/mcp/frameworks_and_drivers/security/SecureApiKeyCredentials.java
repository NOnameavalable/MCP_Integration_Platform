package csc207.mcp.frameworks_and_drivers.security;

import csc207.mcp.use_case.create_api_key.ApiKeyCredentials;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/** 256 random secret bits; SHA-256 encoding for gateway verification and indexed lookup. */
public final class SecureApiKeyCredentials implements ApiKeyCredentials {
    private final SecureRandom random = new SecureRandom();
    @Override public String generateSecret() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return "mcp_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    @Override public String hashSecret(String secret) {
        Objects.requireNonNull(secret);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
            return "sha256$" + Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException failure) {
            throw new IllegalStateException("API key hashing is unavailable", failure);
        }
    }
    public boolean matches(String secret, String encodedHash) {
        if (secret == null || encodedHash == null) { return false; }
        return MessageDigest.isEqual(hashSecret(secret).getBytes(StandardCharsets.UTF_8),
                encodedHash.getBytes(StandardCharsets.UTF_8));
    }
}
