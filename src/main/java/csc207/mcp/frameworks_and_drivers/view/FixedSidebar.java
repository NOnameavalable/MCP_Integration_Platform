package csc207.mcp.frameworks_and_drivers.view;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class FixedSidebar extends Sidebar {

    private static final double WIDTH = 50;

    public FixedSidebar(Runnable onToggle, Runnable onHome) {
        getStyleClass().add("fixed-sidebar");
        var homeIconUrl = Objects.requireNonNull(FixedSidebar.class.getResource("/icons/lucide/house.png"));
        ImageView homeIcon = new ImageView(new Image(homeIconUrl.toExternalForm()));
        homeIcon.setFitWidth(20);
        homeIcon.setFitHeight(20);
        homeIcon.setPreserveRatio(true);
        Button homeButton = new Button();
        homeButton.setGraphic(homeIcon);
        homeButton.getStyleClass().add("sidebar-button");
        homeButton.setAccessibleText("Home");
        homeButton.setTooltip(new Tooltip("Home"));
        homeButton.setOnAction(event -> onHome.run());
        setMinWidth(WIDTH);
        setPrefWidth(WIDTH);
        setMaxWidth(WIDTH);

        var iconUrl = Objects.requireNonNull(FixedSidebar.class.getResource("/icons/lucide/panel-left.png"));
        ImageView icon = new ImageView(new Image(iconUrl.toExternalForm()));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        icon.setPreserveRatio(true);

        Button toggleButton = new Button();
        toggleButton.getStyleClass().add("sidebar-button");
        toggleButton.setGraphic(icon);
        toggleButton.setAccessibleText("Toggle sidebar");
        toggleButton.setTooltip(new Tooltip("Toggle sidebar"));
        toggleButton.setMaxWidth(Double.MAX_VALUE);
        toggleButton.setOnAction(event -> onToggle.run());
        getChildren().addAll(homeButton, toggleButton);
    }
}
