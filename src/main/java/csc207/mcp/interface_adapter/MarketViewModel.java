package csc207.mcp.interface_adapter;

import csc207.mcp.entity.Mcp;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/** JavaFX-observable marketplace state shared by LoadMcps, SearchMarket, and AddToToolkit. */
public final class MarketViewModel {
    private final ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>(
            this, "state", new State(false, List.of(), ""));

    /** The view can observe/bind this property but cannot replace its state. */
    public ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public State getState() {
        return state.get();
    }

    public boolean isLoaded() {
        return getState().isLoaded();
    }

    public List<Mcp> getMcps() {
        return getState().getMcps();
    }

    public String getErrorMessage() {
        return getState().getErrorMessage();
    }

    /** Updates the catalog after a successful load; call on the JavaFX application thread. */
    public void showSuccess(List<Mcp> mcps) {
        state.set(new State(true, mcps, ""));
    }

    /** Records a load failure; call on the JavaFX application thread. */
    public void showFailure(String errorMessage) {
        state.set(new State(false, List.of(), errorMessage));
    }

    /** An immutable snapshot; each completed load emits one complete update. */
    public static final class State {
        private final boolean loaded;
        private final List<Mcp> mcps;
        private final String errorMessage;

        private State(boolean loaded, List<Mcp> mcps, String errorMessage) {
            this.loaded = loaded;
            this.mcps = List.copyOf(mcps);
            this.errorMessage = errorMessage;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public List<Mcp> getMcps() {
            return mcps;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
