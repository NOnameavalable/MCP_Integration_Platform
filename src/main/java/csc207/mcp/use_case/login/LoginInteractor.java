package csc207.mcp.use_case.login;

import csc207.mcp.entity.User;

/**
 * Authenticates users using the stored password hash.
 */
public class LoginInteractor implements LoginInputBoundary {
    private final LoginDataAccessInterface loginDataAccess;
    private final LoginOutputBoundary loginPresenter;
    private final PasswordVerifier passwordVerifier;

    public LoginInteractor(LoginDataAccessInterface loginDataAccess, LoginOutputBoundary loginPresenter,
                           PasswordVerifier passwordVerifier) {
        this.loginDataAccess = loginDataAccess;
        this.loginPresenter = loginPresenter;
        this.passwordVerifier = passwordVerifier;
    }
    @Override
    public void excute(LoginInputData data) {
        User user = loginDataAccess.findByIdentifier(data.getIdentifier());
        if(user == null) {
            loginPresenter.prepareFailView("You are not a User, please try again");
        }
        else if(!passwordVerifier.matches(data.getPassword(), user.getPasswordHash())) {
            loginPresenter.prepareFailView("Wrong Password");
        }
        else {
            LoginOutputData outputData = new LoginOutputData(
                    user.getUserId(), user.getUsername(), user.getEmail(),
                    user.getPasswordHash(), user.getMcpkit());
            loginPresenter.prepareSuccessView(outputData);
        }
    }
}
