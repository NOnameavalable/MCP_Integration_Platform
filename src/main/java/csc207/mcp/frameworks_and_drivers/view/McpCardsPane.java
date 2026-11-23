package csc207.mcp.frameworks_and_drivers.view;

import java.util.List;
import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/** Scrollable area for MCP cards. */
public final class McpCardsPane extends VBox {

    private final CardContainer cardContainer;
    private final ScrollPane scrollPane;
    private final DoubleBinding scrollOffset;

    /** Creates a scrollable cards pane without a top overlay spacer or clip (e.g., for Toolkit). */
    public McpCardsPane() {
        this(new FlowCardContainer(), null, null);
    }

    /** Creates a scrollable cards pane with a custom container and no top overlay spacer or clip. */
    public McpCardsPane(CardContainer cardContainer) {
        this(cardContainer, null, null);
    }

    /** Creates a scrollable cards pane with a top overlay spacer and clip (e.g., for Home). */
    public McpCardsPane(ObservableDoubleValue topSpacerHeight, ObservableDoubleValue clipTop) {
        this(new FlowCardContainer(), topSpacerHeight, clipTop);
    }

    public McpCardsPane(CardContainer cardContainer,
                        ObservableDoubleValue topSpacerHeight,
                        ObservableDoubleValue clipTop) {
        this.cardContainer = Objects.requireNonNull(cardContainer);
        Pane cardsView = Objects.requireNonNull(cardContainer.getView());

        getStylesheets().add(Objects.requireNonNull(
                McpCardsPane.class.getResource("/styles/mcp-cards-scroll.css"),
                "Missing /styles/mcp-cards-scroll.css").toExternalForm());

        VBox content = new VBox();
        if (topSpacerHeight != null) {
            Region topSpacer = new Region();
            // Keep the scroll range independent of the collapsing clip.
            topSpacer.prefHeightProperty().bind(topSpacerHeight);
            topSpacer.setMinHeight(USE_PREF_SIZE);
            content.getChildren().add(topSpacer);
        }
        VBox.setVgrow(cardsView, Priority.ALWAYS);
        content.getChildren().add(cardsView);

        scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("mcp-cards-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMinSize(0, 0);

        DoubleBinding viewportHeight = Bindings.createDoubleBinding(
                () -> scrollPane.getViewportBounds().getHeight(), scrollPane.viewportBoundsProperty());

        ObservableDoubleValue visibleHeight;
        if (clipTop != null) {
            Rectangle clip = new Rectangle();
            clip.yProperty().bind(clipTop);
            clip.widthProperty().bind(scrollPane.widthProperty());
            clip.heightProperty().bind(Bindings.max(0, Bindings.subtract(scrollPane.heightProperty(), clipTop)));
            scrollPane.setClip(clip);
            visibleHeight = clip.heightProperty();
        } else {
            visibleHeight = scrollPane.heightProperty();
        }

        DoubleBinding scrollableHeight = Bindings.createDoubleBinding(
                () -> Math.max(0, content.getLayoutBounds().getHeight() - viewportHeight.get()),
                content.layoutBoundsProperty(), viewportHeight);
        // The ScrollPane and external ScrollBar both use the default 0–1 range.
        scrollOffset = scrollableHeight.multiply(scrollPane.vvalueProperty());
        ScrollBar scrollBar = createVerticalScrollBar(scrollPane, visibleHeight, scrollableHeight);
        StackPane.setAlignment(scrollBar, Pos.BOTTOM_RIGHT);
        StackPane layers = new StackPane(scrollPane, scrollBar);
        layers.setMinSize(0, 0);

        setMinSize(0, 0);
        setVgrow(layers, Priority.ALWAYS);
        getChildren().add(layers);
    }

    /** Replaces the displayed cards and resets scrolling back to the top. */
    public void setCards(List<? extends Node> cards) {
        cardContainer.setCards(cards);
        scrollPane.setVvalue(0);
    }

    ObservableDoubleValue scrollOffset() {
        return scrollOffset;
    }

    private static ScrollBar createVerticalScrollBar(ScrollPane scrollPane,
                                                     ObservableDoubleValue visibleHeight,
                                                     DoubleBinding scrollableHeight) {
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.getStyleClass().add("mcp-cards-scroll-bar");
        scrollBar.setAccessibleText("Scroll MCP cards");
        scrollBar.setMaxWidth(USE_PREF_SIZE);
        scrollBar.setMinHeight(0);
        scrollBar.setMaxHeight(USE_PREF_SIZE);
        scrollBar.prefHeightProperty().bind(visibleHeight);

        scrollBar.setMax(1);
        scrollBar.setUnitIncrement(0.04);
        scrollBar.setBlockIncrement(0.25);
        scrollBar.valueProperty().bindBidirectional(scrollPane.vvalueProperty());
        scrollBar.visibleProperty().bind(
                scrollableHeight.greaterThan(0).and(Bindings.greaterThan(visibleHeight, 0)));
        scrollBar.visibleAmountProperty().bind(Bindings.createDoubleBinding(() -> {
            double currentVisibleHeight = visibleHeight.get();
            double totalHeight = currentVisibleHeight + scrollableHeight.get();
            return totalHeight > 0 ? currentVisibleHeight / totalHeight : 1;
        }, visibleHeight, scrollableHeight));
        return scrollBar;
    }
}
