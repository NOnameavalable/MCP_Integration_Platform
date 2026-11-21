package csc207.mcp.interface_adapter.revoke_api_key;

import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyInputBoundary;
import csc207.mcp.use_case.revoke_api_key.RevokeApiKeyInputData;
import java.util.Objects;

/** The application supplies the authenticated user ID from login state. */
public final class RevokeApiKeyController {
    private final RevokeApiKeyInputBoundary useCase;
    public RevokeApiKeyController(RevokeApiKeyInputBoundary useCase) { this.useCase = Objects.requireNonNull(useCase); }
    public void execute(String userId, String keyId) { useCase.execute(new RevokeApiKeyInputData(userId, keyId)); }
}
