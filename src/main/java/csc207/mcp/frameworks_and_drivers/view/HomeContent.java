package csc207.mcp.frameworks_and_drivers.view;

import csc207.mcp.interface_adapter.account.AuthStateListener;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

/** The platform's home view. */
public final class HomeContent extends ContentView implements AuthStateListener {
    private final OverlayLayer overlay;

    public HomeContent(Runnable onLogin, Runnable onSignup) {
        StackPane layers = new StackPane();
        layers.setMinSize(0, 0);

        overlay = new OverlayLayer(widthProperty(), heightProperty(), layers.heightProperty(), onLogin, onSignup);
        StackPane.setAlignment(overlay, Pos.TOP_CENTER);

        McpCardsPane cards = new McpCardsPane(overlay.expandedHeight(), overlay.heightProperty());
        overlay.bindScrollOffset(cards.scrollOffset());
        layers.getChildren().addAll(cards, overlay);
        setFillingBodyContent(layers);
    }

    @Override
    public void onAuthStateChanged(boolean loggedIn) {
        overlay.applyAuthState(loggedIn);
    }
}
