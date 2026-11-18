package csc207.mcp.app;

import csc207.mcp.frameworks_and_drivers.view.Main;
import javafx.application.Application;

public class Launcher {

    public static void main(String[] args) {
        // JavaFX 25 requires opt-in for HeaderBar and StageStyle.EXTENDED.
        System.setProperty("javafx.enablePreview", "true");
        Application.launch(Main.class, args);
    }
}
