package csc207.mcp.interface_adapter.mcpkit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class McpkitViewModel {
    private final ObjectProperty<McpkitState> state = new SimpleObjectProperty<>(new McpkitState());

    public void setState(McpkitState newState) {
        this.state.set(newState);
    }

    public McpkitState getState() {
        return this.state.get();
    }

    public ReadOnlyObjectProperty<McpkitState> stateProperty() {
        return this.state;
    }
}
