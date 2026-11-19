package csc207.mcp.frameworks_and_drivers.view;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try (InputStream font = Main.class.getResourceAsStream("/fonts/audiowide/Audiowide-Regular.ttf")) {
            if (font == null || Font.loadFont(font, -1) == null) {
                throw new IOException("Could not load the bundled Audiowide font");
            }
        }

        Header header = new PlatformHeader();
        ToggleableSidebar expandable = new ExpandableSidebar();
        Sidebar fixed = new FixedSidebar(expandable::toggle);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setTop(header);
        root.setLeft(new HBox(fixed, expandable));

        ContentView content = new HomeContent();
        content.getStyleClass().add("main-content");

        PseudoClass sidebarVisible = PseudoClass.getPseudoClass("sidebar-visible");
        content.pseudoClassStateChanged(sidebarVisible, expandable.isVisible());
        expandable.visibleProperty().addListener((observable, wasVisible, isVisible) ->
                content.pseudoClassStateChanged(sidebarVisible, isVisible));

        root.setCenter(content);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().addAll(stylesheet("background.css"), stylesheet("main-content.css"));

        stage.setScene(scene);
        header.configureWindow(stage);
        stage.show();
    }

    private static String stylesheet(String name) {
        String path = "/styles/" + name;
        return Objects.requireNonNull(Main.class.getResource(path), "Missing " + path).toExternalForm();
    }
}
