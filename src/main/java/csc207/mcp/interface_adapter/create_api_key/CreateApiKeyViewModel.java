package csc207.mcp.interface_adapter.create_api_key;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/** Observable immutable result snapshots. Contains no raw API key or hash. */
public final class CreateApiKeyViewModel {
    private final ReadOnlyObjectWrapper<Result> state = new ReadOnlyObjectWrapper<>(
            this, "state", new Result(false, "", "", null, ""));
    public ReadOnlyObjectProperty<Result> stateProperty() { return state.getReadOnlyProperty(); }
    public Result getState() { return state.get(); }
    void showSuccess(String keyId, String displayPrefix, java.time.Instant createdAt) { state.set(new Result(true, keyId, displayPrefix, createdAt, "")); }
    void showFailure(String errorMessage) { state.set(new Result(false, "", "", null, errorMessage)); }
    public static final class Result {
        private final boolean successful;
        private final String keyId;
        private final String displayPrefix;
        private final java.time.Instant createdAt;
        private final String errorMessage;
        private Result(boolean successful, String keyId, String displayPrefix, java.time.Instant createdAt, String errorMessage) {
            this.successful = successful;
            this.keyId = keyId;
            this.displayPrefix = displayPrefix;
            this.createdAt = createdAt;
            this.errorMessage = errorMessage;
        }
        public boolean isSuccessful() { return successful; }
        public String getKeyId() { return keyId; }
        public String getErrorMessage() { return errorMessage; }
        public String getDisplayPrefix() { return displayPrefix; }
        public java.time.Instant getCreatedAt() { return createdAt; }
    }
}
