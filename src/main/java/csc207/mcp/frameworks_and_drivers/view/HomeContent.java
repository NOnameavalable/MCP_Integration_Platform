package csc207.mcp.frameworks_and_drivers.view;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

/** The platform's home view. */
public final class HomeContent extends ContentView {

    public HomeContent() {
        StackPane layers = new StackPane();
        layers.setMinSize(0, 0);

        OverlayLayer overlay = new OverlayLayer(widthProperty(), heightProperty(), layers.heightProperty());
        StackPane.setAlignment(overlay, Pos.TOP_CENTER);

        McpCardsPane cards = new McpCardsPane(overlay.expandedHeight(), overlay.heightProperty());
        overlay.bindScrollOffset(cards.scrollOffset());
        layers.getChildren().addAll(cards, overlay);
        setFillingBodyContent(layers);
    }
}
