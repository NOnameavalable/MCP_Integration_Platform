package csc207.mcp.use_case.signup;

/** Submitted registration values. Do not log this object or retain it in a view model. */
public final class SignupInputData {
    private final String username;
    private final String email;
    private final String password;
    private final String repeatPassword;

    public SignupInputData(String username, String email, String password, String repeatPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRepeatPassword() { return repeatPassword; }
}
