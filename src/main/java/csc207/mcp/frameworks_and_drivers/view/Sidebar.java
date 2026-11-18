package csc207.mcp.frameworks_and_drivers.view;

import javafx.scene.layout.VBox;

public abstract class Sidebar extends VBox {

    protected Sidebar() {
        getStyleClass().add("sidebar");
    }
}
