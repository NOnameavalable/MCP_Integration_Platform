package csc207.mcp.use_case.login;

/** Verifies login passwords without exposing a concrete hashing implementation. */
public interface PasswordVerifier {
    /** Compares the supplied plaintext password to the stored hash encoding. */
    boolean matches(String password, String encodedHash);
}
