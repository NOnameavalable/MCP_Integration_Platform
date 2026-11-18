package csc207.mcp.interface_adapter.signup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Framework-independent presentation data. Never stores passwords or their hashes. */
public final class SignupViewModel {
    public static final String RESULT_PROPERTY = "signupResult";
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private boolean successful;
    private String userId = "";
    private String username = "";
    private String email = "";
    private String errorMessage = "";

    public boolean isSuccessful() { return successful; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getErrorMessage() { return errorMessage; }

    void showSuccess(String userId, String username, String email) {
        this.successful = true;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.errorMessage = "";
        notifyResult();
    }

    void showFailure(String errorMessage) {
        this.successful = false;
        this.userId = "";
        this.username = "";
        this.email = "";
        this.errorMessage = errorMessage;
        notifyResult();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

    private void notifyResult() {
        // Null old value ensures repeated identical errors still notify the view.
        changes.firePropertyChange(RESULT_PROPERTY, null, this);
    }
}
