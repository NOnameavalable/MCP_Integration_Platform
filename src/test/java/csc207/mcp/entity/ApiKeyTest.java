package csc207.mcp.entity;

import java.time.Instant;

/** Dependency-free business-rule checks; run main in IntelliJ or with java. */
public final class ApiKeyTest {
    private static int checks;
    private static final Instant CREATED = Instant.parse("2026-11-17T00:00:00Z");

    private ApiKeyTest() { }

    public static void main(String[] args) {
        ApiKey key = fresh();
        check(key.getKeyId().equals("k1"), "record identifier");
        check(key.getOwnerUserId().equals("u1"), "owner identifier");
        check(key.getKeyHash().equals("encoded-hash"), "verification hash");
        check(key.getDisplayPrefix().equals("mcpr_ab12"), "display prefix");
        check(key.getCreatedAt().equals(CREATED), "creation time");
        check(key.isActive(), "new key active");
        check(key.getLastUsedAt() == null, "new key unused");
        check(key.getRevokedAt() == null, "new key not revoked");
        check(key.belongsTo("u1"), "owner recognized");
        check(!key.belongsTo("u2") && !key.belongsTo(null), "other or absent owner rejected");

        key.markUsed(CREATED.plusSeconds(10));
        key.markUsed(CREATED.plusSeconds(5));
        check(key.getLastUsedAt().equals(CREATED.plusSeconds(10)), "latest use never moves backwards");
        expect(IllegalArgumentException.class, () -> key.revoke(CREATED.plusSeconds(9)));
        check(key.isActive(), "failed revocation leaves active state");
        key.revoke(CREATED.plusSeconds(20));
        check(!key.isActive(), "revoked key inactive");
        key.revoke(CREATED.plusSeconds(30));
        check(key.getRevokedAt().equals(CREATED.plusSeconds(20)), "repeat revocation retains first time");
        expect(IllegalStateException.class, () -> key.markUsed(CREATED.plusSeconds(40)));
        check(key.getLastUsedAt().equals(CREATED.plusSeconds(10)), "rejected use leaves history unchanged");

        ApiKey restored = new ApiKey("k1", "u1", "encoded-hash", "mcpr_ab12", CREATED,
                CREATED.plusSeconds(10), CREATED.plusSeconds(20));
        check(!restored.isActive(), "restored revocation retained");
        check(restored.getLastUsedAt().equals(CREATED.plusSeconds(10)), "restored usage retained");
        ApiKey restoredActive = new ApiKey("k2", "u1", "hash", "mcpr_cd34", CREATED,
                CREATED, null);
        check(restoredActive.isActive(), "restored active key");
        restoredActive.revoke(CREATED);
        check(!restoredActive.isActive(), "equal timestamps allowed");

        for (int field = 0; field < 4; field++) {
            for (String invalid : new String[] {null, "", " \t\n"}) {
                String[] values = {"k1", "u1", "hash", "mcpr_ab12"};
                values[field] = invalid;
                expect(IllegalArgumentException.class,
                        () -> new ApiKey(values[0], values[1], values[2], values[3], CREATED));
            }
        }
        expect(IllegalArgumentException.class, () -> new ApiKey("k1", "u1", "hash", "prefix", null));
        expect(IllegalArgumentException.class, () -> new ApiKey("k1", "u1", "hash", "prefix",
                CREATED, CREATED.minusSeconds(1), null));
        expect(IllegalArgumentException.class, () -> new ApiKey("k1", "u1", "hash", "prefix",
                CREATED, null, CREATED.minusSeconds(1)));
        expect(IllegalArgumentException.class, () -> new ApiKey("k1", "u1", "hash", "prefix",
                CREATED, CREATED.plusSeconds(2), CREATED.plusSeconds(1)));
        ApiKey invalidUse = fresh();
        expect(IllegalArgumentException.class, () -> invalidUse.markUsed(CREATED.minusSeconds(1)));
        expect(IllegalArgumentException.class, () -> invalidUse.markUsed(null));
        expect(IllegalArgumentException.class, () -> invalidUse.revoke(CREATED.minusSeconds(1)));
        expect(IllegalArgumentException.class, () -> invalidUse.revoke(null));
        check(invalidUse.isActive() && invalidUse.getLastUsedAt() == null, "invalid actions leave state unchanged");
        System.out.println("ApiKey entity checks passed: " + checks);
    }

    private static ApiKey fresh() {
        return new ApiKey("k1", "u1", "encoded-hash", "mcpr_ab12", CREATED);
    }

    private static void expect(Class<? extends RuntimeException> type, Runnable operation) {
        try {
            operation.run();
        } catch (RuntimeException failure) {
            if (type.isInstance(failure)) {
                checks++;
                return;
            }
            throw failure;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }
}
