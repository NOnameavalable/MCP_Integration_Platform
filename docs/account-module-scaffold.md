# Account module scaffold

Status: package and type skeletons only; no business logic or runnable application yet.

Branch: `feature/account-module-scaffold`.

## Layout

- `entity`: User, ApiKey.
- `use_case/<use_case>`: InputBoundary, InputData, Interactor, OutputBoundary, OutputData, and data-access interfaces where needed.
- `interface_adapter/<use_case>`: Controller and Presenter.
- `interface_adapter/signup`: SignupState and SignupViewModel; `interface_adapter/account`: shared AccountViewModel without a separate AccountState.
- Frontend-owned SignupView and AccountView are not included in this personal branch; coordinate their integration with the frontend teammate.

Use cases: signup, view_profile, create_api_key, revoke_api_key.

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
- `src/main/java/csc207/mcp/interface_adapter/account/AccountViewModel.java`
- `src/main/java/csc207/mcp/interface_adapter/create_api_key/CreateApiKeyController.java`
- `src/main/java/csc207/mcp/interface_adapter/create_api_key/CreateApiKeyPresenter.java`
- `src/main/java/csc207/mcp/interface_adapter/revoke_api_key/RevokeApiKeyController.java`
- `src/main/java/csc207/mcp/interface_adapter/revoke_api_key/RevokeApiKeyPresenter.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupController.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupPresenter.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupState.java`
- `src/main/java/csc207/mcp/interface_adapter/signup/SignupViewModel.java`
- `src/main/java/csc207/mcp/interface_adapter/view_profile/ViewProfileController.java`
- `src/main/java/csc207/mcp/interface_adapter/view_profile/ViewProfilePresenter.java`
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
- `src/main/java/csc207/mcp/use_case/view_profile/ViewProfileDataAccessInterface.java`
- `src/main/java/csc207/mcp/use_case/view_profile/ViewProfileInputBoundary.java`
- `src/main/java/csc207/mcp/use_case/view_profile/ViewProfileInputData.java`
- `src/main/java/csc207/mcp/use_case/view_profile/ViewProfileInteractor.java`
- `src/main/java/csc207/mcp/use_case/view_profile/ViewProfileOutputBoundary.java`
- `src/main/java/csc207/mcp/use_case/view_profile/ViewProfileOutputData.java`

Login and Logout are outside this personal scaffold and belong to the appropriate teammate.

## View Profile

User story: As a logged-in user, I want to click Profile and view my username, email, and API key summaries, so that I can review my account and manage my keys.

Use Case: View Profile

**Primary actor:** A logged-in user.

**Preconditions:** The user has an active authenticated session; the Profile action is available.

**Trigger:** The user clicks Profile.

**Success outcome:** AccountView displays the current user's username, email, and API key summaries. Passwords, raw API keys, and hashes are not displayed.

**Main success scenario:**

1. The user clicks Profile.
2. The system identifies the current authenticated user.
3. The system retrieves the user's username, email, and API key records.
4. The system prepares safe API key summaries.
5. The system opens or updates AccountView with the profile information.

**Extensions:**

- **2a.** The user is no longer authenticated: the system requests login and displays no profile data.
- **3a.** The account cannot be found: the system reports that the profile is unavailable; no unrelated profile is displayed.
- **3b.** Profile or key data cannot be retrieved: the system displays a load error and offers a retry; an error is not presented as an empty key list.
- **3c.** The user has no keys: the system displays the username, email, and an empty-key message; the use case succeeds.
- **3d.** A legacy account has no saved email: the system displays “Not provided”; it does not invent an email.

Implementation plan: ViewProfileController -> ViewProfileInputBoundary -> ViewProfileInteractor -> user/key data-access interfaces -> ViewProfileOutputBoundary / ViewProfilePresenter -> AccountViewModel -> AccountView.

AccountView remains the page name; ViewProfile is the use-case name. CreateApiKey and RevokeApiKey remain separate use cases and can refresh the profile after success. AccountViewModel will hold username, email, and safe key summaries; this scaffold still has no working fields or UI binding.

Email integration: add email to signup input, validate it under the agreed project rules, persist it on User through the DAO, and include it in the profile result. Do not imply that email verification or editing is implemented. Concrete persistence and frontend integration must be coordinated with the relevant teammates.

## Frontend ownership update

The empty SignupView and AccountView skeletons have been removed locally so the frontend teammate can provide the views. The view names in the specifications describe integration targets, not files supplied by this branch. AccountViewModel remains the presenter-to-view contract. AccountState has also been removed; presentation fields will be stored directly in AccountViewModel. These local changes have not been committed or pushed.
