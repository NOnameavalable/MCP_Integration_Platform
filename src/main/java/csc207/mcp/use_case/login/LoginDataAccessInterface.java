package csc207.mcp.use_case.login;

import csc207.mcp.entity.User;


/**
 * Data access boundary skeleton for Login.
 */
public interface LoginDataAccessInterface {
    /**
     *
     * @param identifier
     * @return User if finded, else return None
     */
    User findByIdentifier(String identifier);
}
