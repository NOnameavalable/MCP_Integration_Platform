package csc207.mcp.entity;

/**
 * Account-module scaffold. TODO: implement userId, username, email, and passwordHash.
 * Each user owns a Toolkit; its contents are implemented by the toolkit module.
 * Email must be collected and persisted before it can be displayed in a profile.
 */
public class User {
    private Toolkit toolkit = new Toolkit();

    /** Returns this user's toolkit. */
    public Toolkit getToolkit() {
        return toolkit;
    }
}
