package csc207.mcp.use_case.view_profile;

import csc207.mcp.entity.ApiKey;
import csc207.mcp.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Loads the requested authenticated account and projects records into safe output DTOs. */
public final class ViewProfileInteractor implements ViewProfileInputBoundary {
    private final ViewProfileDataAccessInterface dataAccess;
    private final ViewProfileOutputBoundary presenter;
    public ViewProfileInteractor(ViewProfileDataAccessInterface dataAccess, ViewProfileOutputBoundary presenter) {
        this.dataAccess = Objects.requireNonNull(dataAccess);
        this.presenter = Objects.requireNonNull(presenter);
    }
    @Override public void execute(ViewProfileInputData input) {
        if (input == null || input.getUserId() == null || input.getUserId().isBlank()) {
            presenter.prepareFailView("Please log in before viewing your profile."); return;
        }
        ViewProfileOutputData output;
        try {
            Optional<User> found = Objects.requireNonNull(dataAccess.findUserById(input.getUserId()));
            if (found.isEmpty() || !found.get().getUserId().equals(input.getUserId())) {
                output = ViewProfileOutputData.failure("The account is unavailable.");
            } else {
                List<ViewProfileOutputData.ApiKeySummary> summaries = new ArrayList<>();
                for (ApiKey key : dataAccess.findApiKeysByUserId(input.getUserId())) {
                    if (!key.belongsTo(input.getUserId())) { throw new IllegalStateException("Unexpected key owner"); }
                    summaries.add(new ViewProfileOutputData.ApiKeySummary(key.getKeyId(), key.getDisplayPrefix(),
                            key.getCreatedAt(), key.getLastUsedAt(), key.getRevokedAt()));
                }
                User user = found.get();
                output = new ViewProfileOutputData(user.getUsername(), user.getEmail(), summaries);
            }
        } catch (RuntimeException failure) {
            output = ViewProfileOutputData.failure("The profile could not be loaded. Please try again.");
        }
        if (output.isSuccessful()) { presenter.prepareSuccessView(output); }
        else { presenter.prepareFailView(output.getErrorMessage()); }
    }
}
