package csc207.mcp.frameworks_and_drivers.view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PlatformHeader extends Header {

    public PlatformHeader() {
        Label title = new Label("MCP Platform");
        title.getStyleClass().add("header-title");
        title.setMouseTransparent(true);

        setLeading(title);
    }

    @Override
    public void configureWindow(Stage stage) {
        stage.initStyle(StageStyle.EXTENDED);
        stage.setTitle("MCP Integration Platform");
        Scene scene = stage.getScene();
        scene.getRoot().applyCss();

        setPrefButtonHeight(stage, prefHeight(-1));
        scene.setFill(getBackground().getFills().getFirst().getFill());
    }
}
