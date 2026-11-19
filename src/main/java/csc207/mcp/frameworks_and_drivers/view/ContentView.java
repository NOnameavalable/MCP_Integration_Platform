package csc207.mcp.frameworks_and_drivers.view;

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Shared outer background, rounded panel, and body layout for content pages. */
public abstract class ContentView extends StackPane {

    private final VBox body = new VBox();

    protected ContentView() {
        getStyleClass().add("content-view");
        getStylesheets().add(Objects.requireNonNull(
                ContentView.class.getResource("/styles/main-content.css"),
                "Missing /styles/main-content.css").toExternalForm());

        StackPane panel = new StackPane(body);
        panel.getStyleClass().add("content-panel");
        panel.setMinSize(0, 0);

        body.getStyleClass().add("content-body");
        body.setMinWidth(0);
        body.setMaxHeight(USE_PREF_SIZE);
        getChildren().add(panel);
    }

    /** Sets the page-specific controls while retaining the shared body layout. */
    protected final void setBodyContent(Node... content) {
        body.getChildren().setAll(content);
    }

    /** Lets a page's layout fill the height inside the shared panel. */
    protected final void setFillingBodyContent(Node content) {
        body.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(content, Priority.ALWAYS);
        setBodyContent(content);
    }
}
