package csc207.mcp.use_case.view_profile;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.User;
import java.util.List;
import java.util.Optional;

/** Read-only data operations required to view the current user's profile. */
public interface ViewProfileDataAccessInterface {

    /**
     * Loads the account for a user identified by authenticated context.
     *
     * @param userId the current authenticated user's identifier
     * @return the account, or empty if it no longer exists
     * @throws IllegalStateException if storage cannot be queried
     */
    Optional<User> findUserById(String userId);

    /**
     * Loads only keys owned by the specified authenticated user.
     * Entity records are for internal use; the interactor must convert them
     * to safe summaries before passing results to the presentation layer.
     *
     * @param userId the current authenticated user's identifier
     * @return the user's keys; an empty list if none exist, never null
     * @throws IllegalStateException if keys cannot be loaded (not an empty-list result)
     */
    List<ApiKey> findApiKeysByUserId(String userId);
}
