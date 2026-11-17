package csc207.mcp.entity;

/**
 * A registered account and its owned toolkit.
 * Password hashing, username uniqueness, authentication, and persistence belong
 * to collaborating use cases and adapters, not this entity.
 */
public final class User {
    private final String userId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Toolkit toolkit;

    /** Creates a new account with its own initially empty toolkit. */
    public User(String userId, String username, String email, String passwordHash) {
        this(userId, username, email, passwordHash, new Toolkit());
    }

    /**
     * Creates or restores an account with an existing toolkit.
     * String values must be nonblank and are retained without normalization.
     * Email format and password-policy checks are performed by the signup use case.
     *
     * @throws IllegalArgumentException if a required value is missing or blank
     */
    public User(String userId, String username, String email,
                String passwordHash, Toolkit toolkit) {
        this.userId = requireNonBlank(userId, "userId");
        this.username = requireNonBlank(username, "username");
        this.email = requireNonBlank(email, "email");
        this.passwordHash = requireNonBlank(passwordHash, "passwordHash");
        if (toolkit == null) {
            throw new IllegalArgumentException("toolkit must not be null");
        }
        this.toolkit = toolkit;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    /** For authentication/persistence only; never include in profile output or logs. */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** Returns the owned toolkit; its internal operations belong to Toolkit. */
    public Toolkit getToolkit() {
        return toolkit;
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
