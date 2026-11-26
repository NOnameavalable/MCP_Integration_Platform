package csc207.mcp.frameworks_and_drivers.view;

import csc207.mcp.interface_adapter.MarketViewModel;
import csc207.mcp.interface_adapter.Session;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class Main extends Application {
    private final Session session = new Session();
    private final MarketViewModel marketViewModel = new MarketViewModel();

    @Override
    public void start(Stage stage) throws IOException {
        try (InputStream font = Main.class.getResourceAsStream("/fonts/audiowide/Audiowide-Regular.ttf")) {
            if (font == null || Font.loadFont(font, -1) == null) {
                throw new IOException("Could not load the bundled Audiowide font");
            }
        }

        BorderPane root = new BorderPane();
        Header header = new PlatformHeader();
        ContentView signup = new SignupContent();
        ContentView login = new LoginContent(() -> root.setCenter(signup));
        ContentView account = new AccountContent();
        ContentView toolkit = new ToolkitContent();
        ContentView gateway = new McpGatewayContent();
        HomeContent home = new HomeContent(() -> root.setCenter(login), () -> root.setCenter(signup));
        ToggleableSidebar expandable = new ExpandableSidebar(root.widthProperty(),
                () -> root.setCenter(account), () -> root.setCenter(toolkit), () -> root.setCenter(gateway));
        Sidebar fixed = new FixedSidebar(expandable::toggle, () -> root.setCenter(home));

        root.getStyleClass().add("root-pane");
        root.setTop(header);
        root.setLeft(new HBox(fixed, expandable));

        session.addAuthStateListener(home);
        home.onAuthStateChanged(session.isLoggedIn());

        PseudoClass sidebarVisible = PseudoClass.getPseudoClass("sidebar-visible");
        List<ContentView> pages = List.of(home, login, signup, account, toolkit, gateway);
        for (ContentView page : pages) {
            page.pseudoClassStateChanged(sidebarVisible, expandable.isVisible());
        }
        expandable.visibleProperty().addListener((observable, wasVisible, isVisible) -> {
            for (ContentView page : pages) {
                page.pseudoClassStateChanged(sidebarVisible, isVisible);
            }
        });

        root.setCenter(home);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(stylesheet("background.css"));

        stage.setScene(scene);
        header.configureWindow(stage);
        stage.show();
    }

    private static String stylesheet(String name) {
        String path = "/styles/" + name;
        return Objects.requireNonNull(Main.class.getResource(path), "Missing " + path).toExternalForm();
    }
}
