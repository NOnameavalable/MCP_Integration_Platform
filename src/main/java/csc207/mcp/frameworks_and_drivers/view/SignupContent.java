package csc207.mcp.frameworks_and_drivers.view;

import csc207.mcp.interface_adapter.signup.SignupController;
import csc207.mcp.interface_adapter.signup.SignupViewModel;
import java.util.Objects;
import java.util.concurrent.Executor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Registration form using the shared content panel and observable signup results. */
public final class SignupContent extends ContentView {
  private final TextField username = new TextField();
  private final TextField email = new TextField();
  private final PasswordField password = new PasswordField();
  private final PasswordField repeatPassword = new PasswordField();
  private final Label message = new Label();
  private final Button submit = new Button("Create account");
  private final BooleanProperty busy = new SimpleBooleanProperty(false);
  private final SignupController controller;
  private final Executor executor;

  /** The application supplies the use case, its model, navigation and worker executor. */
  public SignupContent(SignupController controller, SignupViewModel viewModel,
      Runnable showLogin, Executor executor) {
    this.controller = Objects.requireNonNull(controller);
    this.executor = Objects.requireNonNull(executor);
    Objects.requireNonNull(viewModel);
    Objects.requireNonNull(showLogin);
    getStylesheets().add(Objects.requireNonNull(
        getClass().getResource("/styles/signup.css")).toExternalForm());

    Label title = new Label("Create your account");
    title.getStyleClass().add("signup-title");
    Label subtitle = new Label("Your toolkit. Your keys. One place.");
    subtitle.getStyleClass().add("signup-subtitle");
    subtitle.setWrapText(true);
    username.setId("signup-username");
    email.setId("signup-email");
    password.setId("signup-password");
    repeatPassword.setId("signup-repeat-password");
    username.setPromptText("Choose a username");
    email.setPromptText("you@example.com");
    password.setPromptText("Enter a password");
    repeatPassword.setPromptText("Enter your password again");

    message.setId("signup-message");
    message.getStyleClass().add("signup-message");
    message.setWrapText(true);
    message.managedProperty().bind(message.textProperty().isNotEmpty());
    message.visibleProperty().bind(message.managedProperty());
    message.setMaxWidth(Double.MAX_VALUE);
    submit.setId("signup-submit");
    submit.getStyleClass().addAll("overlay-auth-button", "overlay-signup-button");
    submit.setMaxWidth(Double.MAX_VALUE);
    submit.setMinHeight(44);
    submit.disableProperty().bind(busy);
    submit.textProperty().bind(javafx.beans.binding.Bindings.when(busy)
        .then("Creating account…").otherwise("Create account"));
    submit.setOnAction(event -> submit());
    repeatPassword.setOnAction(event -> submit());

    Button login = new Button("Log in");
    login.setId("signup-login");
    login.getStyleClass().addAll("overlay-auth-button", "overlay-login-button");
    login.disableProperty().bind(busy);
    login.setOnAction(event -> {
      clearPasswords();
      message.setText("");
      showLogin.run();
    });
    HBox loginRow = new HBox(4, new Label("Already have an account?"), login);
    loginRow.setAlignment(Pos.CENTER);
    ProgressIndicator progress = new ProgressIndicator();
    progress.setMaxSize(20, 20);
    progress.visibleProperty().bind(busy);
    progress.managedProperty().bind(busy);
    HBox progressRow = new HBox(progress);
    progressRow.setAlignment(Pos.CENTER);

    progressRow.managedProperty().bind(busy);
    VBox form = new VBox(8, title, subtitle,
        field("Username", username), field("Email", email),
        field("Password", password), field("Confirm password", repeatPassword),
        message, submit, progressRow, loginRow);
    form.getStyleClass().add("signup-form");
    form.setMaxWidth(420);
    VBox wrapper = new VBox(form);
    wrapper.setAlignment(Pos.CENTER);
    wrapper.getStyleClass().add("signup-wrapper");
    ScrollPane scroll = new ScrollPane(wrapper);
    scroll.setFitToWidth(true);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.getStyleClass().add("signup-scroll");
    scroll.setMinSize(0, 0);
    setFillingBodyContent(scroll);

    for (TextField field : new TextField[] {username, email, password, repeatPassword}) {
      field.disableProperty().bind(busy);
    }
    viewModel.stateProperty().addListener((observable, oldResult, result) -> {
      busy.set(false);
      clearPasswords();
      message.setText(result.getErrorMessage());
      if (result.isSuccessful()) {
        username.clear();
        email.clear();
      }
    });
    // Leaving through the sidebar must not leave password fields populated.
    parentProperty().addListener((observable, oldParent, newParent) -> {
      if (newParent == null) {
        clearPasswords();
      }
    });
  }

  private VBox field(String text, TextField input) {
    Label label = new Label(text);
    label.setLabelFor(input);
    label.getStyleClass().add("signup-field-label");
    input.getStyleClass().add("signup-input");
    input.setMaxWidth(Double.MAX_VALUE);
    return new VBox(6, label, input);
  }

  private void submit() {
    if (busy.get()) {
      return;
    }
    String submittedUsername = username.getText();
    String submittedEmail = email.getText();
    String submittedPassword = password.getText();
    String confirmation = repeatPassword.getText();
    message.setText("");
    busy.set(true);
    Task<Void> task = new Task<>() {
      @Override
      protected Void call() {
        controller.execute(submittedUsername, submittedEmail, submittedPassword, confirmation);
        return null;
      }
    };
    task.setOnFailed(event -> {
      busy.set(false);
      clearPasswords();
      message.setText("Registration is temporarily unavailable. Please try again.");
    });
    try {
      executor.execute(task);
    } catch (RuntimeException failure) {
      busy.set(false);
      clearPasswords();
      message.setText("Registration is temporarily unavailable. Please try again.");
    }
  }

  private void clearPasswords() {
    password.clear();
    repeatPassword.clear();
  }
}
