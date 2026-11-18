package csc207.mcp.use_case.login;

/**
 * Carries the username or email address and password for a login request.
 * Input validation and authentication are handled by the login interactor.
 */
public class LoginInputData {
    /** The username or email address entered by the user. */
    private final String identifier;

    /** The password entered by the user. */
    private final String password;

    /**
     * Creates the input data for a login request.
     *
     * @param identifier the user's username or email address
     * @param password the user's password
     */
    public LoginInputData(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    /**
     * Returns the username or email address supplied for login.
     *
     * @return the login identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the password supplied for login.
     *
     * @return the login password
     */
    public String getPassword() {
        return password;
    }
}
