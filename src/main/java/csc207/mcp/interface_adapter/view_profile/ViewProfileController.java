package csc207.mcp.interface_adapter.view_profile;

import csc207.mcp.use_case.view_profile.ViewProfileInputBoundary;
import csc207.mcp.use_case.view_profile.ViewProfileInputData;
import java.util.Objects;

/** The application supplies the authenticated user ID from login state. */
public final class ViewProfileController {
    private final ViewProfileInputBoundary useCase;
    public ViewProfileController(ViewProfileInputBoundary useCase) { this.useCase = Objects.requireNonNull(useCase); }
    public void execute(String userId) { useCase.execute(new ViewProfileInputData(userId)); }
}
