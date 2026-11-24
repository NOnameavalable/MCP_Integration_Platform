package csc207.mcp.frameworks_and_drivers.view;

import csc207.mcp.app.SignupFactory;
import csc207.mcp.entity.User;
import csc207.mcp.frameworks_and_drivers.data_access_object.HttpDbClient;
import csc207.mcp.frameworks_and_drivers.data_access_object.HttpUserDataAccessObject;
import csc207.mcp.interface_adapter.Session;
import csc207.mcp.interface_adapter.signup.SignupViewModel;
import csc207.mcp.use_case.signup.SignupDataAccessInterface;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Composes the desktop pages and their application dependencies. */
public class Main extends Application {
    private final Session session = new Session();
    private final ExecutorService signupWorker = Executors.newSingleThreadExecutor(action -> {
        Thread worker = new Thread(action, "signup-worker");
        worker.setDaemon(true);
        return worker;
    });

    @Override
    public void start(Stage stage) throws IOException {
        try (InputStream font = Main.class.getResourceAsStream("/fonts/audiowide/Audiowide-Regular.ttf")) {
            if (font == null || Font.loadFont(font, -1) == null) {
                throw new IOException("Could not load the bundled Audiowide font");
            }
        }

        BorderPane root = new BorderPane();
        Header header = new PlatformHeader();
        ContentView login = new LoginContent();
        SignupViewModel signupModel = new SignupViewModel();
        var signupController = SignupFactory.create(signupDataAccess(), signupModel, () -> {
            root.setCenter(login);
            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.initOwner(stage);
            confirmation.setTitle("Account created");
            confirmation.setHeaderText("Welcome, " + signupModel.getUsername() + "!");
            confirmation.setContentText("You can now log in with your username or email.");
            confirmation.show();
        });
        ContentView signup = new SignupContent(signupController, signupModel,
                () -> root.setCenter(login), signupWorker);
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

    @Override
    public void stop() {
        signupWorker.shutdownNow();
    }

    /** Lazily connects registration to the configured remote database API. */
    private SignupDataAccessInterface signupDataAccess() {
        return new SignupDataAccessInterface() {
            private HttpUserDataAccessObject delegate;

            private HttpUserDataAccessObject dao() {
                if (delegate == null) {
                    String baseUrl = System.getProperty("mcp.gateway.url",
                            System.getenv("MCP_GATEWAY_URL"));
                    String secret = System.getProperty("mcp.internal.secret",
                            System.getenv("MCP_INTERNAL_SECRET"));
                    if (baseUrl == null || baseUrl.isBlank() || secret == null || secret.isBlank()) {
                        throw new IllegalStateException("Remote registration is not configured");
                    }
                    delegate = new HttpUserDataAccessObject(new HttpDbClient(baseUrl, secret));
                }
                return delegate;
            }

            @Override
            public boolean existsByUsername(String username) {
                return dao().existsByUsername(username);
            }

            @Override
            public boolean existsByEmail(String email) {
                return dao().existsByEmail(email);
            }

            @Override
            public void save(User user) {
                dao().save(user);
            }
        };
    }

    private static String stylesheet(String name) {
        String path = "/styles/" + name;
        return Objects.requireNonNull(Main.class.getResource(path), "Missing " + path).toExternalForm();
    }
}
