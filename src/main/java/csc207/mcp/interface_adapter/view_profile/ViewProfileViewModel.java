package csc207.mcp.interface_adapter.view_profile;

import csc207.mcp.use_case.view_profile.ViewProfileOutputData;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/** Observable safe profile DTO snapshots; never stores credentials or entities. */
public final class ViewProfileViewModel {
    private final ReadOnlyObjectWrapper<ViewProfileOutputData> state =
            new ReadOnlyObjectWrapper<>(this, "state", ViewProfileOutputData.failure(""));
    public ReadOnlyObjectProperty<ViewProfileOutputData> stateProperty() { return state.getReadOnlyProperty(); }
    public ViewProfileOutputData getState() { return state.get(); }
    void setResult(ViewProfileOutputData result) { state.set(result); }
}
