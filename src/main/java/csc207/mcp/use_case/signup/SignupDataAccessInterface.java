package csc207.mcp.use_case.signup;

import csc207.mcp.entity.User;

/** Data operations required by the signup use case. */
public interface SignupDataAccessInterface {

    /**
     * Saves a new user, including their email and password hash.
     * The implementation must reject duplicate usernames or emails without overwriting
     * an existing account. Uniqueness must be enforced during the insert.
     *
     * @param user the new user to persist
     * @throws IllegalArgumentException if the username or email is already registered
     * @throws IllegalStateException if the user cannot be saved
     */
    void save(User user);
}
