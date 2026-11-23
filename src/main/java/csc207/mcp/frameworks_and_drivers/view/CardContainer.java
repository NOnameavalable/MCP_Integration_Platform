package csc207.mcp.frameworks_and_drivers.view;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/** Layout container responsible for arranging and displaying MCP cards inside {@link McpCardsPane}. */
public interface CardContainer {

    /** Returns the JavaFX pane mounted inside the scrollable viewport. */
    Pane getView();

    /** Replaces the currently displayed cards or placeholder nodes. */
    void setCards(List<? extends Node> cards);
}
