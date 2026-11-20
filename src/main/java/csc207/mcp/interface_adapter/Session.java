package csc207.mcp.interface_adapter;

import csc207.mcp.interface_adapter.account.AuthStateListener;
import csc207.mcp.use_case.login.LoginOutputData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Holds the current user's identity. Both getters return null when signed out.
 * Access the desktop session on the JavaFX application thread. Notifications are synchronous.
 */
public final class Session {
    private final List<AuthStateListener> authStateListeners = new ArrayList<>();
    private LoginOutputData currentUser;

    public void addAuthStateListener(AuthStateListener listener) {
        authStateListeners.add(Objects.requireNonNull(listener));
    }

    /** Records the identity returned by a successful login. */
    public void login(LoginOutputData data) {
        Objects.requireNonNull(data);
        String userId = data.getUserId();
        String username = data.getUsername();
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID must not be blank.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank.");
        }
        boolean wasLoggedIn = isLoggedIn();
        currentUser = data;
        if (!wasLoggedIn) {
            notifyAuthStateChanged(true);
        }
    }

    public void logout() {
        if (!isLoggedIn()) {
            return;
        }
        currentUser = null;
        notifyAuthStateChanged(false);
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public String getUserId() {
        return currentUser == null ? null : currentUser.getUserId();
    }

    public String getUsername() {
        return currentUser == null ? null : currentUser.getUsername();
    }

    private void notifyAuthStateChanged(boolean loggedIn) {
        for (AuthStateListener listener : List.copyOf(authStateListeners)) {
            listener.onAuthStateChanged(loggedIn);
        }
    }
}
