package csc207.mcp.interface_adapter.signup;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/** JavaFX-observable presentation state; never stores passwords or their hashes. */
public final class SignupViewModel {
    private final ReadOnlyObjectWrapper<Result> state = new ReadOnlyObjectWrapper<>(
            this, "state", new Result(false, "", "", "", ""));

    /** The view can observe/bind this property but cannot replace the state. */
    public ReadOnlyObjectProperty<Result> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public Result getState() { return state.get(); }
    public boolean isSuccessful() { return getState().isSuccessful(); }
    public String getUserId() { return getState().getUserId(); }
    public String getUsername() { return getState().getUsername(); }
    public String getEmail() { return getState().getEmail(); }
    public String getErrorMessage() { return getState().getErrorMessage(); }

    // Presenter calls these on the JavaFX application thread in the running UI.
    void showSuccess(String userId, String username, String email) {
        state.set(new Result(true, userId, username, email, ""));
    }

    void showFailure(String errorMessage) {
        state.set(new Result(false, "", "", "", errorMessage));
    }

    /**
     * One immutable snapshot makes each result observable as a single update.
     * Each attempt gets a new object, so repeated identical errors still notify.
     * Kept inside the ViewModel; there is no separate SignupState file.
     */
    public static final class Result {
        private final boolean successful;
        private final String userId;
        private final String username;
        private final String email;
        private final String errorMessage;

        private Result(boolean successful, String userId, String username,
                       String email, String errorMessage) {
            this.successful = successful;
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccessful() { return successful; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getErrorMessage() { return errorMessage; }
    }
}
