package csc207.mcp.use_case.create_api_key;

/** Credential generation and verification encoding live outside the business rules. */
public interface ApiKeyCredentials {
    String generateSecret();
    String hashSecret(String secret);
}
