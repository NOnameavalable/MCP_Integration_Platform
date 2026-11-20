package csc207.mcp.interface_adapter.revoke_api_key;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/** Observable immutable result snapshots. Contains no raw API key or hash. */
public final class RevokeApiKeyViewModel {
    private final ReadOnlyObjectWrapper<Result> state = new ReadOnlyObjectWrapper<>(
            this, "state", new Result(false, "", false, ""));
    public ReadOnlyObjectProperty<Result> stateProperty() { return state.getReadOnlyProperty(); }
    public Result getState() { return state.get(); }
    void showSuccess(String keyId, boolean alreadyRevoked) { state.set(new Result(true, keyId, alreadyRevoked, "")); }
    void showFailure(String errorMessage) { state.set(new Result(false, "", false, errorMessage)); }
    public static final class Result {
        private final boolean successful;
        private final String keyId;
        private final boolean alreadyRevoked;
        private final String errorMessage;
        private Result(boolean successful, String keyId, boolean alreadyRevoked, String errorMessage) {
            this.successful = successful;
            this.keyId = keyId;
            this.alreadyRevoked = alreadyRevoked;
            this.errorMessage = errorMessage;
        }
        public boolean isSuccessful() { return successful; }
        public String getKeyId() { return keyId; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isAlreadyRevoked() { return alreadyRevoked; }
    }
}
