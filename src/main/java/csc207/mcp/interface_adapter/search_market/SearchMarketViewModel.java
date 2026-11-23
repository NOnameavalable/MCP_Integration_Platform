package csc207.mcp.interface_adapter.search_market;

import csc207.mcp.use_case.search_market.McpServerData;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/** JavaFX-observable state dedicated to marketplace searches. */
public final class SearchMarketViewModel {
    private final ReadOnlyObjectWrapper<Result> state = new ReadOnlyObjectWrapper<>(
            this, "state", new Result(false, "", List.of(), ""));

    /** The view can observe/bind this property but cannot replace its state. */
    public ReadOnlyObjectProperty<Result> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public Result getState() { return state.get(); }
    public boolean isSuccessful() { return getState().isSuccessful(); }
    public String getKeyword() { return getState().getKeyword(); }
    public List<McpServerData> getResults() { return getState().getResults(); }
    public String getErrorMessage() { return getState().getErrorMessage(); }

    // Presenter invokes these on the JavaFX application thread in the running UI.
    void showSuccess(String keyword, List<McpServerData> results) {
        state.set(new Result(true, keyword, results, ""));
    }

    void showFailure(String errorMessage) {
        state.set(new Result(false, "", List.of(), errorMessage));
    }

    /** An immutable snapshot; each completed attempt emits one complete update. */
    public static final class Result {
        private final boolean successful;
        private final String keyword;
        private final List<McpServerData> results;
        private final String errorMessage;

        private Result(boolean successful, String keyword, List<McpServerData> results, String errorMessage) {
            this.successful = successful;
            this.keyword = keyword;
            this.results = List.copyOf(results);
            this.errorMessage = errorMessage;
        }

        public boolean isSuccessful() { return successful; }
        public String getKeyword() { return keyword; }
        public List<McpServerData> getResults() { return results; }
        public String getErrorMessage() { return errorMessage; }
    }
}
