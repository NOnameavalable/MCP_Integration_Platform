package csc207.mcp.use_case.signup;

import csc207.mcp.entity.User;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/** Validates registration, creates a User, saves it, then reports the confirmed result. */
public final class SignupInteractor implements SignupInputBoundary {
    private static final Pattern EMAIL = Pattern.compile("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    private final SignupDataAccessInterface dataAccess;
    private final SignupOutputBoundary presenter;
    private final PasswordHasher passwordHasher;
    private final Supplier<String> userIdGenerator;

    public SignupInteractor(SignupDataAccessInterface dataAccess, SignupOutputBoundary presenter,
                            PasswordHasher passwordHasher) {
        this(dataAccess, presenter, passwordHasher, () -> UUID.randomUUID().toString());
    }

    /** Injectable ID source makes the use case deterministic in tests. */
    public SignupInteractor(SignupDataAccessInterface dataAccess, SignupOutputBoundary presenter,
                            PasswordHasher passwordHasher, Supplier<String> userIdGenerator) {
        this.dataAccess = Objects.requireNonNull(dataAccess);
        this.presenter = Objects.requireNonNull(presenter);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.userIdGenerator = Objects.requireNonNull(userIdGenerator);
    }

    @Override
    public void execute(SignupInputData inputData) {
        String validationError = validate(inputData);
        if (validationError != null) {
            presenter.prepareFailView(validationError);
            return;
        }
        // Normalize outer whitespace only; do not change case or alter passwords.
        String username = inputData.getUsername().strip();
        String email = inputData.getEmail().strip();
        boolean exists;
        try {
            exists = dataAccess.existsByUsername(username);
            if (!exists && dataAccess.existsByEmail(email)) {
                presenter.prepareFailView("This email is already registered.");
                return;
            }
        } catch (IllegalStateException failure) {
            presenter.prepareFailView("Registration is temporarily unavailable. Please try again.");
            return;
        }
        if (exists) {
            presenter.prepareFailView("This username is already registered.");
            return;
        }

        User user;
        try {
            String passwordHash = passwordHasher.hash(inputData.getPassword());
            user = new User(userIdGenerator.get(), username, email, passwordHash);
        } catch (RuntimeException failure) {
            // Do not expose implementation errors or credential material to the UI.
            presenter.prepareFailView("The account could not be prepared. Please try again.");
            return;
        }
        try {
            dataAccess.save(user);
        } catch (IllegalArgumentException conflict) {
            // The DAO must enforce uniqueness even if the earlier checks passed.
            presenter.prepareFailView("This username or email is already registered.");
            return;
        } catch (IllegalStateException failure) {
            presenter.prepareFailView("The account could not be saved. Please try again.");
            return;
        }
        // Presentation errors must not be caught and mislabeled as storage failures.
        presenter.prepareSuccessView(new SignupOutputData(
                user.getUserId(), user.getUsername(), user.getEmail()));
    }

    private String validate(SignupInputData input) {
        if (input == null) { return "Please provide registration information."; }
        if (input.getUsername() == null || input.getUsername().isBlank()) {
            return "Please enter a username.";
        }
        if (input.getEmail() == null || !EMAIL.matcher(input.getEmail().strip()).matches()) {
            return "Please enter a valid email address.";
        }
        if (input.getPassword() == null || input.getPassword().isBlank()) {
            return "Please enter a password.";
        }
        if (!input.getPassword().equals(input.getRepeatPassword())) {
            return "The passwords do not match.";
        }
        return null;
    }
}
