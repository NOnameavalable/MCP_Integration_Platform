package csc207.mcp.frameworks_and_drivers.view;

import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

/** Responsive wrapping flow layout for MCP cards. */
public final class FlowCardContainer implements CardContainer {
    private static final double DEFAULT_GAP = 16;
    private static final Insets DEFAULT_PADDING = new Insets(16, 20, 24, 20);

    private final FlowPane pane;

    public FlowCardContainer() {
        this(DEFAULT_GAP, DEFAULT_GAP, DEFAULT_PADDING);
    }

    public FlowCardContainer(double hgap, double vgap, Insets padding) {
        this.pane = new FlowPane(hgap, vgap);
        this.pane.setPadding(Objects.requireNonNull(padding));
        this.pane.setMinWidth(0);
        this.pane.setMaxWidth(Double.MAX_VALUE);
        this.pane.getStyleClass().add("mcp-cards-flow");
    }

    @Override
    public Pane getView() {
        return pane;
    }

    @Override
    public void setCards(List<? extends Node> cards) {
        Objects.requireNonNull(cards);
        pane.getChildren().setAll(cards);
    }
}
