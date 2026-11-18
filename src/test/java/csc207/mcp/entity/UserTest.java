package csc207.mcp.entity;

/** Dependency-free entity checks; run this class's main method in IntelliJ. */
public final class UserTest {
    private static int checks;

    private UserTest() { }

    public static void main(String[] args) {
        Mcpkit mcpkit = new Mcpkit("u1");
        User restored = new User("u1", "alice", "alice@example.test", "encoded-hash", mcpkit);
        check(restored.getUserId().equals("u1"), "identity retained");
        check(restored.getUsername().equals("alice"), "username retained");
        check(restored.getEmail().equals("alice@example.test"), "email retained");
        check(restored.getPasswordHash().equals("encoded-hash"), "hash retained");
        check(restored.getMcpkit() == mcpkit, "restored mcpkit retained");

        User first = new User("u2", "bob", "bob@example.test", "hash-2");
        User second = new User("u3", "carol", "carol@example.test", "hash-3");
        check(first.getMcpkit() != null, "new account owns a mcpkit");
        check(first.getMcpkit() != second.getMcpkit(), "accounts do not share a default mcpkit");

        for (int field = 0; field < 4; field++) {
            for (String invalid : new String[] {null, "", " \t\n"}) {
                String[] values = {"u1", "alice", "alice@example.test", "encoded-hash"};
                values[field] = invalid;
                expectInvalid(() -> new User(values[0], values[1], values[2], values[3]));
            }
        }
        expectInvalid(() -> new User("u1", "alice", "alice@example.test", "hash", null));
        User unchanged = new User("u4", " Alice ", "Alice@example.test", " hash ");
        check(unchanged.getUsername().equals(" Alice "), "normalization is not silently applied");
        check(unchanged.getPasswordHash().equals(" hash "), "hash is not modified");
        System.out.println("User entity checks passed: " + checks);
    }

    private static void expectInvalid(Runnable operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException expected) {
            checks++;
            return;
        }
        throw new AssertionError("Invalid required value was accepted");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }
}
