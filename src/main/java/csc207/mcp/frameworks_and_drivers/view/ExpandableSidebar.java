package csc207.mcp.frameworks_and_drivers.view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ExpandableSidebar extends ToggleableSidebar {

    private static final double EXPANDED_WIDTH = 200;
    private static final Duration TOGGLE_DURATION = Duration.millis(250);

    private final Timeline animation = new Timeline();
    private boolean expanded = true;

    public ExpandableSidebar() {
        getStyleClass().add("expandable-sidebar");
        setMinWidth(0);
        setPrefWidth(EXPANDED_WIDTH);
        setMaxWidth(USE_PREF_SIZE);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        animation.setOnFinished(event -> {
            setManaged(expanded);
            setVisible(expanded);
        });
    }

    @Override
    public void toggle() {
        double currentWidth = getPrefWidth();
        animation.stop();
        setPrefWidth(currentWidth);

        expanded = !expanded;
        if (expanded) {
            setVisible(true);
            setManaged(true);
        }

        double targetWidth = expanded ? EXPANDED_WIDTH : 0;
        animation.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO, new KeyValue(prefWidthProperty(), currentWidth)),
                new KeyFrame(TOGGLE_DURATION,
                        new KeyValue(prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH)));
        animation.playFromStart();
    }
}
