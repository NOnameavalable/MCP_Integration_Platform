package csc207.mcp.frameworks_and_drivers.view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/** Search input with an animated focus shadow. */
public final class SearchBar extends HBox {

    public SearchBar() {
        TextArea input = new TextArea();
        input.setPromptText("Search MCP servers");
        input.setAccessibleText("Search MCP servers");
        input.setPrefRowCount(1);
        input.setWrapText(true);
        input.setMinWidth(0);
        input.getStyleClass().add("home-search-input");
        setHgrow(input, Priority.ALWAYS);

        getStyleClass().add("home-search-bar");
        getChildren().add(input);

        animateSearchShadow();
        dismissSearchFocusOnOutsideClick(input);
    }

    private void animateSearchShadow() {
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.rgb(35, 52, 82, 0.10), 12, 0, 0, 3);
        setEffect(shadow);

        Duration duration = Duration.millis(180);
        Timeline animation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(shadow.colorProperty(), shadow.getColor()),
                        new KeyValue(shadow.radiusProperty(), shadow.getRadius()),
                        new KeyValue(shadow.offsetYProperty(), shadow.getOffsetY())),
                new KeyFrame(duration,
                        new KeyValue(shadow.colorProperty(), Color.rgb(35, 52, 82, 0.23), Interpolator.EASE_BOTH),
                        new KeyValue(shadow.radiusProperty(), 26, Interpolator.EASE_BOTH),
                        new KeyValue(shadow.offsetYProperty(), 10, Interpolator.EASE_BOTH))
        );

        focusWithinProperty().addListener((observable, wasFocused, focused) -> {
            Duration target = focused ? duration : Duration.ZERO;
            if (animation.getCurrentTime().equals(target)) {
                animation.pause();
            } else {
                animation.setRate(focused ? 1 : -1);
                animation.play();
            }
        });
    }

    private void dismissSearchFocusOnOutsideClick(TextArea input) {
        EventHandler<MouseEvent> dismissFocus = event -> {
            Scene scene = getScene();
            if (scene == null || scene.getFocusOwner() != input) {
                return;
            }

            Node clicked = event.getPickResult().getIntersectedNode();
            for (Node node = clicked; node != null; node = node.getParent()) {
                if (node == this) {
                    return;
                }
            }

            scene.getRoot().requestFocus();
        };

        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, dismissFocus);
            }
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, dismissFocus);
            }
        });
    }
}
