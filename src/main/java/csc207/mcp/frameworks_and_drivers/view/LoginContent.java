package csc207.mcp.frameworks_and_drivers.view;

import csc207.mcp.interface_adapter.login.LoginController;
import csc207.mcp.interface_adapter.login.LoginViewModel;
import java.util.Objects;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Minimal login form inside the shared content panel. */
public final class LoginContent extends ContentView {
    private static final String FIELD_STYLE = """
            -fx-background-color: white;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-width: 1;
            -fx-padding: 12 15;
            -fx-font-size: 14px;
            -fx-text-fill: #333943;
            -fx-prompt-text-fill: #636C7B;
            -fx-highlight-fill: #DCE3ED;
            -fx-highlight-text-fill: #333943;
            """;
    private static final String BUTTON_STYLE = """
            -fx-background-radius: 12;
            -fx-padding: 12 17;
            -fx-cursor: hand;
            """;

    private final TextField identifierInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Button loginButton = new Button();
    private final Label loginButtonText = new Label("Log in");
    private final Hyperlink signupLink = new Hyperlink("Create an account");
    private final Label errorLabel = new Label();
    private LoginController controller;
    private boolean submitting;

    /** Builds the form for the existing application's no-argument page creation. */
    public LoginContent() {
        this(() -> { });
        signupLink.setDisable(true);
    }

    /** Enables signup navigation independently of the login service. */
    public LoginContent(Runnable onSignup) {
        Objects.requireNonNull(onSignup);
        getStyleClass().add("login-content");

        Label title = new Label("Welcome back.");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: normal; -fx-text-fill: #333943;");
        title.setWrapText(true);
        Label subtitle = new Label("Your tools, connected.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #636C7B;");
        VBox heading = new VBox(6, title, subtitle);
        VBox.setMargin(heading, new Insets(0, 0, 10, 0));

        configureField(identifierInput, "Enter your username", "Username or email");
        configureField(passwordInput, "Enter your password", "Password");
        identifierInput.setOnAction(event -> passwordInput.requestFocus());
        passwordInput.setOnAction(event -> submit());

        loginButtonText.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        Label arrow = new Label("↗");
        arrow.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox buttonBody = new HBox(loginButtonText, spacer, arrow);
        buttonBody.setAlignment(Pos.CENTER_LEFT);
        buttonBody.setMaxWidth(Double.MAX_VALUE);
        loginButton.setGraphic(buttonBody);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        buttonBody.prefWidthProperty().bind(loginButton.widthProperty().subtract(34));
        loginButton.setMinHeight(46);
        loginButton.setAccessibleText("Log in");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> submit());
        loginButton.hoverProperty().addListener((observable, before, after) -> styleButton());
        loginButton.focusedProperty().addListener((observable, before, after) -> styleButton());
        styleButton();

        Label signupPrompt = new Label("New to MCP?");
        signupPrompt.setStyle("-fx-font-size: 13px; -fx-text-fill: #636C7B;");
        signupLink.setStyle("""
                -fx-font-size: 13px; -fx-text-fill: #333943;
                -fx-padding: 2 0; -fx-underline: true;
                """);
        signupLink.setOnAction(event -> {
            passwordInput.clear();
            errorLabel.setText("");
            onSignup.run();
        });
        HBox signup = new HBox(6, signupPrompt, signupLink);
        signup.setAlignment(Pos.CENTER);

        errorLabel.setId("login-error");
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #B42318;");
        errorLabel.setWrapText(true);
        errorLabel.setMinHeight(36);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setAlignment(Pos.TOP_CENTER);

        VBox form = new VBox(18, heading,
                fieldGroup("Username or email", identifierInput),
                fieldGroup("Password", passwordInput), loginButton, signup, errorLabel);
        form.setMinWidth(0);
        form.setMaxWidth(330);
        form.setMaxHeight(USE_PREF_SIZE);

        StackPane centeredForm = new StackPane(form);
        centeredForm.setPadding(new Insets(12, 0, 0, 0));
        centeredForm.setStyle("-fx-background-color: #F4F5F7;");
        ScrollPane scroll = new ScrollPane(centeredForm);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setMinSize(0, 0);
        scroll.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        scroll.viewportBoundsProperty().addListener((observable, before, bounds) ->
                centeredForm.setMinHeight(bounds.getHeight()));
        setFillingBodyContent(scroll);
        sceneProperty().addListener((observable, before, scene) -> {
            if (scene == null) {
                passwordInput.clear();
            }
        });
    }

    /** Supplies the existing login chain and the application's signup navigation. */
    public LoginContent(LoginController controller, LoginViewModel viewModel, Runnable onSignup) {
        this(onSignup);
        this.controller = Objects.requireNonNull(controller);
        Objects.requireNonNull(viewModel).stateProperty().addListener((observable, before, result) -> {
            errorLabel.setText(result.getErrorMessage());
            if (result.isSuccessful()) {
                passwordInput.clear();
            }
        });
    }

    private static VBox fieldGroup(String text, TextField input) {
        Label label = new Label(text);
        label.setLabelFor(input);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #333943;");
        VBox group = new VBox(8, label, input);
        group.setMinWidth(0);
        return group;
    }

    private static void configureField(TextField input, String prompt, String accessibleText) {
        input.setPromptText(prompt);
        input.setAccessibleText(accessibleText);
        input.setMinWidth(0);
        input.setMaxWidth(Double.MAX_VALUE);
        input.setMinHeight(46);
        input.setStyle(FIELD_STYLE + "-fx-border-color: #D9DEE6;");
        input.focusedProperty().addListener((observable, before, focused) ->
                input.setStyle(FIELD_STYLE + "-fx-border-color: "
                        + (focused ? "#64748B" : "#D9DEE6") + ";"));
    }

    private void styleButton() {
        boolean highlighted = loginButton.isHover() || loginButton.isFocused();
        loginButton.setStyle(BUTTON_STYLE + "-fx-background-color: "
                + (highlighted ? "#4B5563" : "#374151") + ";"
                + (loginButton.isFocused() ? "-fx-effect: dropshadow(gaussian, #94A3B8, 4, 0.3, 0, 0);" : ""));
    }

    private void submit() {
        if (submitting) {
            return;
        }
        errorLabel.setText("");
        String identifier = identifierInput.getText().strip();
        String password = passwordInput.getText();
        if (identifier.isEmpty()) {
            errorLabel.setText("Please enter your username or email.");
            identifierInput.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            errorLabel.setText("Please enter your password.");
            passwordInput.requestFocus();
            return;
        }
        if (controller == null) {
            errorLabel.setText("Sign-in is currently unavailable. Please try again later.");
            return;
        }

        Task<Void> request = new Task<>() {
            @Override
            protected Void call() {
                controller.execute(identifier, password);
                return null;
            }
        };
        setSubmitting(true);
        request.setOnSucceeded(event -> {
            passwordInput.clear();
            setSubmitting(false);
        });
        request.setOnFailed(event -> {
            passwordInput.clear();
            setSubmitting(false);
            errorLabel.setText("Unable to log in. Please try again.");
        });
        Thread.ofVirtual().name("login-request").start(request);
    }

    private void setSubmitting(boolean value) {
        submitting = value;
        identifierInput.setDisable(value);
        passwordInput.setDisable(value);
        loginButton.setDisable(value);
        signupLink.setDisable(value);
        loginButtonText.setText(value ? "Logging in…" : "Log in");
    }
}
