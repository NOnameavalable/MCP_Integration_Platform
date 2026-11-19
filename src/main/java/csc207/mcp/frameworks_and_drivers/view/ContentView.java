package csc207.mcp.frameworks_and_drivers.view;

import javafx.scene.layout.StackPane;

/** Base class for pages displayed in the main content area. */
public abstract class ContentView extends StackPane {

    protected ContentView() {
        getStyleClass().add("content-view");
    }
}
