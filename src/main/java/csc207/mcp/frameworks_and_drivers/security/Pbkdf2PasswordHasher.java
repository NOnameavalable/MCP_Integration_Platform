package csc207.mcp.frameworks_and_drivers.security;

import csc207.mcp.use_case.signup.PasswordHasher;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** JDK-only password hashing adapter. The encoded format is shared with login. */
public final class Pbkdf2PasswordHasher implements PasswordHasher {
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final String PREFIX = "pbkdf2-sha256";
    private final SecureRandom random = new SecureRandom();

    @Override
    public String hash(String password) {
        Objects.requireNonNull(password);
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] derived = derive(password, salt);
        return PREFIX + "$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derived);
    }

    /** For the login teammate: compare a supplied password to the stored encoding. */
    public boolean matches(String password, String encodedHash) {
        if (password == null || encodedHash == null || encodedHash.length() > 256) {
            return false;
        }
        String[] parts = encodedHash.split("\\$", -1);
        if (parts.length != 4 || !PREFIX.equals(parts[0])
                || !Integer.toString(ITERATIONS).equals(parts[1])) {
            return false;
        }
        byte[] salt;
        byte[] expected;
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            expected = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException malformed) {
            return false;
        }
        if (salt.length != SALT_BYTES || expected.length != HASH_BITS / 8) {
            return false;
        }
        return MessageDigest.isEqual(expected, derive(password, salt));
    }

    private byte[] derive(String password, byte[] salt) {
        char[] characters = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(characters, salt, ITERATIONS, HASH_BITS);
        Arrays.fill(characters, '\0');
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException failure) {
            throw new IllegalStateException("Password hashing is unavailable", failure);
        } finally {
            spec.clearPassword();
        }
    }
}
