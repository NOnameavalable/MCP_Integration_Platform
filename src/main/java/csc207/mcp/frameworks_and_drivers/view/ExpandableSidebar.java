package csc207.mcp.frameworks_and_drivers.view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Objects;

public class ExpandableSidebar extends ToggleableSidebar {

    private static final double WIDTH_FRACTION = 0.20;
    private static final double MIN_EXPANDED_WIDTH = 180;
    private static final double MAX_EXPANDED_WIDTH = 280;
    private static final Duration TOGGLE_DURATION = Duration.millis(250);

    private final Timeline animation = new Timeline();
    private final DoubleProperty expansion = new SimpleDoubleProperty(1);
    private boolean expanded = true;

    public ExpandableSidebar(ObservableDoubleValue layoutWidth) {
        getStyleClass().add("expandable-sidebar");
        setMinWidth(0);
        prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> expansion.get() * Math.clamp(layoutWidth.get() * WIDTH_FRACTION,
                        MIN_EXPANDED_WIDTH, MAX_EXPANDED_WIDTH),
                layoutWidth, expansion));
        setMaxWidth(USE_PREF_SIZE);

        getChildren().addAll(
                navigationButton("Account", "user-round"),
                navigationButton("Toolkit", "wrench"),
                navigationButton("MCP Gateway", "network"));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        animation.setOnFinished(event -> {
            setManaged(expanded);
            setVisible(expanded);
        });
    }

    private static Button navigationButton(String label, String iconName) {
        var iconUrl = Objects.requireNonNull(
                ExpandableSidebar.class.getResource("/icons/lucide/" + iconName + ".png"));
        ImageView icon = new ImageView(new Image(iconUrl.toExternalForm()));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        icon.setPreserveRatio(true);
        Button button = new Button(label, icon);
        button.getStyleClass().addAll("sidebar-button", "navigation-button");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    @Override
    public void toggle() {
        double currentExpansion = expansion.get();
        animation.stop();
        expansion.set(currentExpansion);

        expanded = !expanded;
        if (expanded) {
            setVisible(true);
            setManaged(true);
        }

        double targetExpansion = expanded ? 1 : 0;
        animation.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO, new KeyValue(expansion, currentExpansion)),
                new KeyFrame(TOGGLE_DURATION,
                        new KeyValue(expansion, targetExpansion, Interpolator.EASE_BOTH)));
        animation.playFromStart();
    }
}
