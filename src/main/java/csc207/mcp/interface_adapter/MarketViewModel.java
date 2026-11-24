package csc207.mcp.interface_adapter;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/** Observable marketplace state for the result of an add-to-toolkit request. */
public final class MarketViewModel {
    private final ReadOnlyObjectWrapper<Result> state = new ReadOnlyObjectWrapper<>(
            this, "state", new Result(false, "", "", "", ""));

    public ReadOnlyObjectProperty<Result> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public Result getState() { return state.get(); }

    /** Called by the presenter on the JavaFX application thread. */
    public void showSuccess(String userId, String serverId, String serverName) {
        state.set(new Result(true, userId, serverId, serverName, "Added to toolkit successfully."));
    }

    /** Clears metadata from any earlier successful attempt. */
    public void showFailure(String errorMessage) {
        state.set(new Result(false, "", "", "", errorMessage));
    }

    /** Immutable snapshot of one completed request. */
    public static final class Result {
        private final boolean successful;
        private final String userId;
        private final String serverId;
        private final String serverName;
        private final String message;

        private Result(boolean successful, String userId, String serverId,
                       String serverName, String message) {
            this.successful = successful;
            this.userId = userId;
            this.serverId = serverId;
            this.serverName = serverName;
            this.message = message;
        }

        public boolean isSuccessful() { return successful; }
        public String getUserId() { return userId; }
        public String getServerId() { return serverId; }
        public String getServerName() { return serverName; }
        public String getMessage() { return message; }
    }
}
