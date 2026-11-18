package csc207.mcp.use_case.signup;

/** Port for password hashing; concrete cryptography stays outside the use case. */
public interface PasswordHasher {
    /** Returns a storage-ready encoding including algorithm parameters and salt. */
    String hash(String password);
}
