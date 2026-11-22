package csc207.mcp.frameworks_and_drivers.data_access_object;

import csc207.mcp.entity.User;
import csc207.mcp.use_case.login.LoginDataAccessInterface;
import csc207.mcp.use_case.signup.SignupDataAccessInterface;
import java.util.Objects;
import org.json.JSONObject;

/** Client-side HTTP DAO for user registration and authentication. */
public final class HttpUserDataAccessObject
        implements SignupDataAccessInterface, LoginDataAccessInterface {
    private final HttpDbClient client;

    public HttpUserDataAccessObject(HttpDbClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    @Override
    public boolean existsByUsername(String username) {
        JSONObject req = new JSONObject().put("username", username);
        JSONObject res = client.post("/db/users/exists-by-username", req);
        return res.getBoolean("exists");
    }

    @Override
    public boolean existsByEmail(String email) {
        JSONObject req = new JSONObject().put("email", email);
        JSONObject res = client.post("/db/users/exists-by-email", req);
        return res.getBoolean("exists");
    }

    @Override
    public void save(User user) {
        JSONObject req = new JSONObject()
                .put("userId", user.getUserId())
                .put("username", user.getUsername())
                .put("email", user.getEmail())
                .put("passwordHash", user.getPasswordHash());
        client.post("/db/users/save", req);
    }

    @Override
    public User findByIdentifier(String identifier) {
        JSONObject req = new JSONObject().put("identifier", identifier);
        JSONObject res = client.post("/db/users/find-by-identifier", req);
        if (res.isNull("user")) {
            return null;
        }
        JSONObject obj = res.getJSONObject("user");
        return new User(
                obj.getString("userId"),
                obj.getString("username"),
                obj.getString("email"),
                obj.getString("passwordHash"));
    }
}
