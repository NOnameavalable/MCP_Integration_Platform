package csc207.mcp.use_case.create_api_key;

/** Account-module scaffold. Key-creation business logic is not implemented yet. */
public class CreateApiKeyInteractor implements CreateApiKeyInputBoundary {

    @Override
    public void execute(CreateApiKeyInputData inputData) {
        // TODO: identify the authenticated user, generate a key, save its hash
        // and ownership, then report success or failure through the output boundary.
        throw new UnsupportedOperationException("API key creation is not implemented yet.");
    }
}
