package csc207.mcp.interface_adapter.create_api_key;

import csc207.mcp.use_case.create_api_key.CreateApiKeyInputBoundary;
import csc207.mcp.use_case.create_api_key.CreateApiKeyInputData;
import java.util.Objects;

/** Converts a view action into a use-case request. */
public final class CreateApiKeyController {
    private final CreateApiKeyInputBoundary useCase;
    public CreateApiKeyController(CreateApiKeyInputBoundary useCase) { this.useCase = Objects.requireNonNull(useCase); }
    public void execute() { useCase.execute(new CreateApiKeyInputData()); }
}
