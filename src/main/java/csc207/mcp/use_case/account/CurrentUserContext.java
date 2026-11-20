package csc207.mcp.use_case.account;

/** Trusted session port. Return null when nobody is authenticated; never read an owner from a form. */
@FunctionalInterface
public interface CurrentUserContext {
    String getAuthenticatedUserId();
}
