package csc207.mcp.frameworks_and_drivers.view;

import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/** Scrollable area for MCP cards. */
public final class McpCardsPane extends VBox {

    private final DoubleBinding scrollOffset;

    public McpCardsPane(ObservableDoubleValue topSpacerHeight, ObservableDoubleValue clipTop) {
        getStylesheets().add(Objects.requireNonNull(
                McpCardsPane.class.getResource("/styles/mcp-cards-scroll.css"),
                "Missing /styles/mcp-cards-scroll.css").toExternalForm());

        Region topSpacer = new Region();
        // Keep the scroll range independent of the collapsing clip.
        topSpacer.prefHeightProperty().bind(topSpacerHeight);
        topSpacer.setMinHeight(USE_PREF_SIZE);

        Region placeholder = new Region();
        VBox content = new VBox(topSpacer, placeholder);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("mcp-cards-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMinSize(0, 0);

        // Reserve three viewport heights of blank content for scrolling.
        DoubleBinding viewportHeight = Bindings.createDoubleBinding(
                () -> scrollPane.getViewportBounds().getHeight(), scrollPane.viewportBoundsProperty());
        placeholder.prefHeightProperty().bind(viewportHeight.multiply(3));

        Rectangle clip = new Rectangle();
        clip.yProperty().bind(clipTop);
        clip.widthProperty().bind(scrollPane.widthProperty());
        clip.heightProperty().bind(Bindings.max(0, Bindings.subtract(scrollPane.heightProperty(), clipTop)));
        scrollPane.setClip(clip);

        DoubleBinding scrollableHeight = Bindings.createDoubleBinding(
                () -> Math.max(0, content.getLayoutBounds().getHeight() - viewportHeight.get()),
                content.layoutBoundsProperty(), viewportHeight);
        // The ScrollPane and external ScrollBar both use the default 0–1 range.
        scrollOffset = scrollableHeight.multiply(scrollPane.vvalueProperty());
        ScrollBar scrollBar = createVerticalScrollBar(scrollPane, clip, scrollableHeight);
        StackPane.setAlignment(scrollBar, Pos.BOTTOM_RIGHT);
        StackPane layers = new StackPane(scrollPane, scrollBar);
        layers.setMinSize(0, 0);

        setMinSize(0, 0);
        setVgrow(layers, Priority.ALWAYS);
        getChildren().add(layers);
    }

    ObservableDoubleValue scrollOffset() {
        return scrollOffset;
    }

    private static ScrollBar createVerticalScrollBar(ScrollPane scrollPane, Rectangle clip,
                                                     DoubleBinding scrollableHeight) {
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.getStyleClass().add("mcp-cards-scroll-bar");
        scrollBar.setAccessibleText("Scroll MCP cards");
        scrollBar.setMaxWidth(USE_PREF_SIZE);
        scrollBar.setMinHeight(0);
        scrollBar.setMaxHeight(USE_PREF_SIZE);
        scrollBar.prefHeightProperty().bind(clip.heightProperty());

        scrollBar.setMax(1);
        scrollBar.setUnitIncrement(0.04);
        scrollBar.setBlockIncrement(0.25);
        scrollBar.valueProperty().bindBidirectional(scrollPane.vvalueProperty());
        scrollBar.visibleProperty().bind(scrollableHeight.greaterThan(0).and(clip.heightProperty().greaterThan(0)));
        scrollBar.visibleAmountProperty().bind(Bindings.createDoubleBinding(() -> {
            double visibleHeight = clip.getHeight();
            double totalHeight = visibleHeight + scrollableHeight.get();
            return totalHeight > 0 ? visibleHeight / totalHeight : 1;
        }, clip.heightProperty(), scrollableHeight));
        return scrollBar;
    }
}
