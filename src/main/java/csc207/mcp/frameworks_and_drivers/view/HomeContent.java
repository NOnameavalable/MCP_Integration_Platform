package csc207.mcp.frameworks_and_drivers.view;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

/** The platform's home view. */
public final class HomeContent extends ContentView {

    public HomeContent() {
        VBox layout = new VBox(new SearchBar());
        layout.setMaxWidth(650);
        layout.setMaxHeight(USE_PREF_SIZE);
        setMargin(layout, new Insets(0, 28, 0, 28));
        getChildren().add(layout);
    }
}
