package csc207.mcp.interface_adapter.account;

/** Receives changes to the application's login state. */
@FunctionalInterface
public interface AuthStateListener {
    void onAuthStateChanged(boolean loggedIn);
}
