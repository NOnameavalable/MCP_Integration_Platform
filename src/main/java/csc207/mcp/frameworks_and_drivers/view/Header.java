package csc207.mcp.frameworks_and_drivers.view;

import javafx.scene.layout.HeaderBar;
import javafx.stage.Stage;

public abstract class Header extends HeaderBar {

    protected Header() {
        getStyleClass().add("header-bar");
    }

    /**
     * Configures this header's window after the scene is attached and before the window is shown.
     */
    public abstract void configureWindow(Stage stage);
}
