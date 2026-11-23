package csc207.mcp.frameworks_and_drivers.view;

import csc207.mcp.app.SignupFactory;
import csc207.mcp.frameworks_and_drivers.data_access_object.SqliteDatabaseManager;
import csc207.mcp.frameworks_and_drivers.data_access_object.SqliteUserDataAccessObject;
import csc207.mcp.frameworks_and_drivers.security.Pbkdf2PasswordHasher;
import csc207.mcp.interface_adapter.signup.SignupViewModel;
import csc207.mcp.use_case.signup.SignupDataAccessInterface;
import csc207.mcp.entity.User;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** Real JavaFX controls with an isolated SQLite database and controlled background execution. */
public final class SignupContentTest {
  private static int checks;
  private static SignupContent view;
  private static Stage stage;

  /** Runs on a desktop with JavaFX available; leaves no accounts in the application's database. */
  public static void main(String[] args) throws Exception {
    Path directory = Files.createTempDirectory("signup-ui-test-");
    SqliteDatabaseManager database = new SqliteDatabaseManager(directory.resolve("users.db").toString());
    database.initializeTables();
    SqliteUserDataAccessObject realDao = new SqliteUserDataAccessObject(database);
    AtomicBoolean failSave = new AtomicBoolean();
    SignupDataAccessInterface dao = new SignupDataAccessInterface() {
      public boolean existsByUsername(String username) { return realDao.existsByUsername(username); }
      public boolean existsByEmail(String email) { return realDao.existsByEmail(email); }
      public void save(User user) {
        if (failSave.get()) { throw new IllegalStateException("private database details"); }
        realDao.save(user);
      }
    };
    AtomicReference<Runnable> queued = new AtomicReference<>();
    AtomicInteger navigations = new AtomicInteger();
    AtomicBoolean workerOffFx = new AtomicBoolean();
    Platform.startup(() -> { });
    Platform.setImplicitExit(false);
    try {
      fx(() -> {
        SignupViewModel model = new SignupViewModel();
        var controller = SignupFactory.create(dao, model, navigations::incrementAndGet);
        view = new SignupContent(controller, model, navigations::incrementAndGet, task -> {
          check(queued.compareAndSet(null, task), "one background submission at a time");
        });
        BorderPane root = new BorderPane(view);
        root.setTop(new PlatformHeader());
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(SignupContentTest.class.getResource("/styles/background.css").toExternalForm());
        stage = new Stage();
        stage.setTitle("Signup UI verification");
        stage.setScene(scene);
        stage.show();
        root.applyCss(); root.layout();
        check(view.lookup("#signup-password") instanceof javafx.scene.control.PasswordField,
            "password is masked");
        check(view.lookup("#signup-repeat-password") instanceof javafx.scene.control.PasswordField,
            "confirmation is masked");
        screenshot("signup-form.png");
        fill("alice", "alice@example.test", "secret", "different");
        button("signup-submit").fire();
        check(button("signup-submit").isDisabled(), "submit is disabled while queued");
        check(field("signup-username").isDisabled(), "inputs are disabled while queued");
        check(button("signup-login").isDisabled(), "back navigation is disabled while queued");
        button("signup-submit").fire();
        return null;
      });
      Runnable task = queued.getAndSet(null);
      workerOffFx.set(!Platform.isFxApplicationThread()); task.run();
      fx(() -> {
        check(message().contains("do not match"), "password mismatch appears in the actual view");
        check(!button("signup-submit").isDisabled(), "validation re-enables submission");
        check(field("signup-password").getText().isEmpty(), "failure clears password fields");
        screenshot("signup-validation.png");
        fill("alice", "alice@example.test", "secret", "secret");
        button("signup-submit").fire(); return null;
      });
      queued.getAndSet(null).run();
      fx(() -> {
        check(navigations.get() == 1, "saved registration triggers success navigation once");
        check(field("signup-password").getText().isEmpty()
            && field("signup-email").getText().isEmpty(), "success clears the form");
        fill("alice", "alice@example.test", "secret", "secret");
        button("signup-submit").fire(); return null;
      });
      queued.getAndSet(null).run();
      fx(() -> {
        check(message().contains("already registered"), "duplicate username reaches view");
        check(navigations.get() == 1, "failure does not navigate");
        fill("bob", "invalid-email", "secret", "secret");
        button("signup-submit").fire(); return null;
      });
      queued.getAndSet(null).run();
      fx(() -> {
        check(message().contains("valid email"), "invalid email reaches view");
        fill("bob", "bob@example.test", "secret", "secret");
        failSave.set(true);
        button("signup-submit").fire(); return null;
      });
      queued.getAndSet(null).run();
      fx(() -> {
        check(message().contains("could not be saved") && !message().contains("private"),
            "database failure is sanitized");
        stage.setWidth(540); stage.setHeight(460);
        return null;
      });
      for (int attempt = 0; attempt < 50 && fx(() -> stage.getScene().getWidth()) > 550; attempt++) {
        Thread.sleep(20);
      }
      fx(() -> {
        check(stage.getScene().getWidth() <= 550, "test window actually resized");
        stage.getScene().getRoot().applyCss(); stage.getScene().getRoot().layout();
        check(view.lookup(".scroll-pane") != null, "short windows retain a scrollable form");
        screenshot("signup-small-window.png");
        field("signup-password").setText("clear-on-leave");
        ((BorderPane) stage.getScene().getRoot()).setCenter(new Label("Other page"));
        check(field("signup-password").getText().isEmpty(), "sidebar departure clears secrets");
        stage.close(); return null;
      });
      User saved = new SqliteUserDataAccessObject(new SqliteDatabaseManager(
          directory.resolve("users.db").toString())).findByIdentifier("alice");
      check(saved != null && new Pbkdf2PasswordHasher().matches("secret", saved.getPasswordHash()),
          "account persists and password verifies after reopening DAO");
      check(realDao.findByIdentifier("bob") == null, "failed save creates no account");
      check(workerOffFx.get(), "registration runs off the JavaFX thread");
      System.out.println("Signup JavaFX/SQLite checks passed: " + checks);
    } finally {
      Platform.exit();
    }
  }

  private static void fill(String username, String email, String password, String repeated) {
    field("signup-username").setText(username);
    field("signup-email").setText(email);
    field("signup-password").setText(password);
    field("signup-repeat-password").setText(repeated);
  }

  private static TextField field(String id) { return (TextField) view.lookup("#" + id); }
  private static Button button(String id) { return (Button) view.lookup("#" + id); }
  private static String message() { return ((Label) view.lookup("#signup-message")).getText(); }

  private static <T> T fx(Callable<T> action) throws Exception {
    FutureTask<T> task = new FutureTask<>(action);
    Platform.runLater(task);
    return task.get(15, TimeUnit.SECONDS);
  }

  private static void screenshot(String name) throws Exception {
    String output = System.getProperty("signup.screenshot.dir");
    if (output == null) { return; }
    stage.getScene().getRoot().applyCss(); stage.getScene().getRoot().layout();
    WritableImage image = stage.getScene().snapshot(null);
    BufferedImage png = new BufferedImage((int) image.getWidth(), (int) image.getHeight(),
        BufferedImage.TYPE_INT_ARGB);
    int[] pixels = new int[png.getWidth() * png.getHeight()];
    image.getPixelReader().getPixels(0, 0, png.getWidth(), png.getHeight(),
        javafx.scene.image.PixelFormat.getIntArgbInstance(), pixels, 0, png.getWidth());
    png.setRGB(0, 0, png.getWidth(), png.getHeight(), pixels, 0, png.getWidth());
    Files.createDirectories(Path.of(output));
    ImageIO.write(png, "png", Path.of(output, name).toFile());
  }

  private static void check(boolean condition, String message) {
    if (!condition) { throw new AssertionError(message); }
    checks++;
  }
}
