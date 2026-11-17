package csc207.mcp.use_case.signup;

import csc207.mcp.entity.User;

/** Data operations required by the signup use case. */
public interface SignupDataAccessInterface {

    /**
     * Checks whether the username is already registered.
     *
     * @param username the username to check
     * @return true if an account with this username exists
     * @throws IllegalStateException if storage cannot be queried
     */
    boolean existsByUsername(String username);

    /**
     * Saves a new user, including their email and password hash.
     * The implementation must reject duplicate usernames without overwriting
     * an existing account, even if a prior availability check succeeded.
     *
     * @param user the new user to persist
     * @throws IllegalArgumentException if the username is already registered
     * @throws IllegalStateException if the user cannot be saved
     */
    void save(User user);
}
