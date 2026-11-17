# Account module scaffold

Status: package and type skeletons only; no business logic or runnable application yet.

Branch: `feature/account-module-scaffold`.

## Layout

- `entity`: User, ApiKey.
- `use_case/<use_case>`: InputBoundary, InputData, Interactor, OutputBoundary, OutputData, and data-access interfaces where needed.
- `interface_adapter/<use_case>`: Controller and Presenter.
- `interface_adapter/signup`, `account`: page-specific State and ViewModel.
- `frameworks_and_drivers/view`: SignupView, AccountView (Swing JPanel skeletons).

Use cases: signup, view_api_keys, create_api_key, revoke_api_key.

## Clean Architecture relationships

- Each Interactor implements its InputBoundary.
- Each Presenter implements its OutputBoundary.
- Controllers will depend on InputBoundary; interactors will depend on OutputBoundary and inner-layer data-access interfaces.
- Concrete data access, persistence, hashing, session implementation, application assembly, and gateway code are not implemented here.
- AccountView shares AccountViewModel across listing, creation, and revocation.

## Next steps

Define input/output fields and boundary methods, then implement entities, use-case logic, adapters, UI binding, and meaningful tests. Agree with teammates on persistence and session interfaces before adding concrete implementations.

## Files

- `src/main/java/csc207/mcp/entity/ApiKey.java`
- `src/main/java/csc207/mcp/entity/User.java`
- `src/main/java/csc207/mcp/frameworks_and_drivers/view/AccountView.java`
- `src/main/java/csc207/mcp/frameworks_and_drivers/view/SignupView.java`
- `src/main/java/csc207/mcp/interface_adapter/account/AccountState.java`
- `src/main/java/csc207/mcp/interface_adapter/account/AccountViewModel.java`
- `src/main/java/csc207/mcp/interface_adapter/create_api_key/CreateApiKeyController.java`
- `src/main/java/csc207/mcp/interface_adapter/create_api_key/CreateApiKeyPresenter.java`
- `src/main/java/csc207/mcp/interface_adapter/revoke_api_key/RevokeApiKeyController.java`
- `src/main/java/csc207/mcp/interface_adapter/revoke_api_key/RevokeApiKeyPresenter.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupController.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupPresenter.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupState.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupViewModel.java`
- `src/main/java/csc207/mcp/interface_adapter/view_api_keys/ViewApiKeysController.java`
- `src/main/java/csc207/mcp/interface_adapter/view_api_keys/ViewApiKeysPresenter.java`
- `src/main/java/csc207/mcp/use_case/create_api_key/CreateApiKeyDataAccessInterface.java`
- `src/main/java/csc207/mcp/use_case/create_api_key/CreateApiKeyInputBoundary.java`
- `src/main/java/csc207/mcp/use_case/create_api_key/CreateApiKeyInputData.java`
- `src/main/java/csc207/mcp/use_case/create_api_key/CreateApiKeyInteractor.java`
- `src/main/java/csc207/mcp/use_case/create_api_key/CreateApiKeyOutputBoundary.java`
- `src/main/java/csc207/mcp/use_case/create_api_key/CreateApiKeyOutputData.java`
- `src/main/java/csc207/mcp/use_case/revoke_api_key/RevokeApiKeyDataAccessInterface.java`
- `src/main/java/csc207/mcp/use_case/revoke_api_key/RevokeApiKeyInputBoundary.java`
- `src/main/java/csc207/mcp/use_case/revoke_api_key/RevokeApiKeyInputData.java`
- `src/main/java/csc207/mcp/use_case/revoke_api_key/RevokeApiKeyInteractor.java`
- `src/main/java/csc207/mcp/use_case/revoke_api_key/RevokeApiKeyOutputBoundary.java`
- `src/main/java/csc207/mcp/use_case/revoke_api_key/RevokeApiKeyOutputData.java`
- `src/main/java/csc207/mcp/use_case/signup/SignupDataAccessInterface.java`
- `src/main/java/csc207/mcp/use_case/signup/SignupInputBoundary.java`
- `src/main/java/csc207/mcp/use_case/signup/SignupInputData.java`
- `src/main/java/csc207/mcp/use_case/signup/SignupInteractor.java`
- `src/main/java/csc207/mcp/use_case/signup/SignupOutputBoundary.java`
- `src/main/java/csc207/mcp/use_case/signup/SignupOutputData.java`
- `src/main/java/csc207/mcp/use_case/view_api_keys/ViewApiKeysDataAccessInterface.java`
- `src/main/java/csc207/mcp/use_case/view_api_keys/ViewApiKeysInputBoundary.java`
- `src/main/java/csc207/mcp/use_case/view_api_keys/ViewApiKeysInputData.java`
- `src/main/java/csc207/mcp/use_case/view_api_keys/ViewApiKeysInteractor.java`
- `src/main/java/csc207/mcp/use_case/view_api_keys/ViewApiKeysOutputBoundary.java`
- `src/main/java/csc207/mcp/use_case/view_api_keys/ViewApiKeysOutputData.java`

Login and Logout are outside this personal scaffold and belong to the appropriate teammate.
