package csc207.mcp.frameworks_and_drivers.view;

import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/** Positions search and account controls within the home view. */
public final class OverlayLayer extends Pane {

    private static final double SEARCH_WIDTH_FRACTION = 0.85;
    private static final double SEARCH_HEIGHT_FRACTION = 0.075;
    private static final double MIN_SEARCH_HEIGHT = 44;
    private static final double MAX_SEARCH_HEIGHT = 60;
    private static final double AUTH_BUTTON_HEIGHT_FRACTION = 36.0 / 655.0;
    private static final double MIN_AUTH_BUTTON_HEIGHT = 32;
    private static final double MAX_AUTH_BUTTON_HEIGHT = 44;
    private static final double CONTENT_TOP = 0;
    private static final double COLLAPSED_HEIGHT = 64;
    private static final double SEARCH_RESIZE_LEAD = 16;
    private static final double COLLAPSED_SEARCH_LEFT_INSET = 7;
    private static final double COLLAPSED_SEARCH_WIDTH_FRACTION = 0.9;
    private static final double SEARCH_BUTTON_GAP = 12;

    private final SearchBar searchBar = new SearchBar();
    private final HBox authControls = new HBox(4);
    private final DoubleBinding expandedHeight;

    public OverlayLayer(ReadOnlyDoubleProperty contentWidth, ReadOnlyDoubleProperty contentHeight,
                        ObservableDoubleValue availableHeight) {
        searchBar.maxWidthProperty().bind(contentWidth.multiply(SEARCH_WIDTH_FRACTION));
        DoubleBinding searchHeight = Bindings.createDoubleBinding(
                () -> Math.clamp(contentHeight.get() * SEARCH_HEIGHT_FRACTION, MIN_SEARCH_HEIGHT, MAX_SEARCH_HEIGHT),
                contentHeight);
        searchBar.prefHeightProperty().bind(searchHeight);
        searchBar.setMaxHeight(USE_PREF_SIZE);

        // Extend from the panel top to the bottom edge of the previously centered search bar.
        expandedHeight = Bindings.createDoubleBinding(() -> {
            double height = availableHeight.get();
            return Math.min(height, (height + searchHeight.get()) / 3);
        }, availableHeight, searchHeight);
        prefHeightProperty().bind(expandedHeight);
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        setPickOnBounds(false);

        DoubleBinding authButtonHeight = Bindings.createDoubleBinding(
                () -> Math.clamp(contentHeight.get() * AUTH_BUTTON_HEIGHT_FRACTION,
                        MIN_AUTH_BUTTON_HEIGHT, MAX_AUTH_BUTTON_HEIGHT),
                contentHeight);
        Button loginButton = new Button("Log in");
        loginButton.getStyleClass().addAll("overlay-auth-button", "overlay-login-button");
        loginButton.prefHeightProperty().bind(authButtonHeight);
        Button signupButton = new Button("Sign up");
        signupButton.getStyleClass().addAll("overlay-auth-button", "overlay-signup-button");
        signupButton.prefHeightProperty().bind(authButtonHeight);
        authControls.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        authControls.setPickOnBounds(false);
        authControls.getChildren().addAll(loginButton, signupButton);

        getChildren().addAll(searchBar, authControls);
    }

    ObservableDoubleValue expandedHeight() {
        return expandedHeight;
    }

    void bindScrollOffset(ObservableDoubleValue scrollOffset) {
        prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
            double initialHeight = expandedHeight.get();
            double minimumHeight = Math.min(initialHeight, COLLAPSED_HEIGHT);
            return Math.max(minimumHeight, initialHeight - scrollOffset.get());
        }, expandedHeight, scrollOffset));
    }

    @Override
    protected void layoutChildren() {
        layoutInArea(authControls, 0, CONTENT_TOP, getWidth(), getHeight(), -1, HPos.RIGHT, VPos.TOP);
        layoutSearchBar();
    }

    private void layoutSearchBar() {
        double searchHeight = searchBar.getPrefHeight();
        double buttonTop = authControls.getLayoutY();
        double buttonHeight = authControls.getHeight();
        double buttonBottom = buttonTop + buttonHeight;

        double startHeight = Math.min(expandedHeight.get(), buttonBottom + searchHeight + SEARCH_RESIZE_LEAD);
        double endHeight = Math.min(expandedHeight.get(), COLLAPSED_HEIGHT);
        double resizeDistance = startHeight - endHeight;
        if (resizeDistance <= 0 || getHeight() >= startHeight) {
            layoutInArea(searchBar, 0, 0, getWidth(), getHeight(), -1, HPos.CENTER, VPos.BOTTOM);
            return;
        }

        double progress = Math.clamp((startHeight - getHeight()) / resizeDistance, 0, 1);
        double enterRowAt = Math.clamp((startHeight - buttonBottom - searchHeight) / resizeDistance, 0.2, 0.8);
        double approachProgress = Math.min(1, progress / enterRowAt);
        double rowProgress = Math.max(0, (progress - enterRowAt) / (1 - enterRowAt));

        Interpolator easing = Interpolator.EASE_BOTH;
        double expandedWidth = Math.min(getWidth(), searchBar.maxWidth(-1));
        double widthBesideButtons = Math.max(0, authControls.getLayoutX() - COLLAPSED_SEARCH_LEFT_INSET);
        double collapsedWidth = Math.min(expandedWidth * COLLAPSED_SEARCH_WIDTH_FRACTION,
                Math.max(0, widthBesideButtons - SEARCH_BUTTON_GAP));
        // Leave room to keep narrowing after the search bar clears the buttons.
        double widthAtRowEntry = Math.min(easing.interpolate(expandedWidth, collapsedWidth, enterRowAt),
                widthBesideButtons - Math.min(SEARCH_BUTTON_GAP, widthBesideButtons) / 2);
        double width = progress <= enterRowAt
                ? easing.interpolate(expandedWidth, widthAtRowEntry, approachProgress)
                : easing.interpolate(widthAtRowEntry, collapsedWidth, rowProgress);
        double x = easing.interpolate((getWidth() - expandedWidth) / 2,
                COLLAPSED_SEARCH_LEFT_INSET, approachProgress);
        double height = easing.interpolate(searchHeight, buttonHeight, rowProgress);
        double yAtRowEntry = Math.max(buttonBottom, startHeight - resizeDistance * enterRowAt - searchHeight);
        double y = progress <= enterRowAt
                ? Math.max(buttonBottom, getHeight() - searchHeight)
                : easing.interpolate(yAtRowEntry, CONTENT_TOP, rowProgress);
        searchBar.resizeRelocate(x, y, width, height);
    }
}
