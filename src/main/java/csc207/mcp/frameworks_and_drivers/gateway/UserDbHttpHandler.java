package csc207.mcp.frameworks_and_drivers.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import csc207.mcp.entity.User;
import csc207.mcp.frameworks_and_drivers.data_access_object.SqliteUserDataAccessObject;
import java.io.IOException;
import java.util.Objects;
import org.json.JSONObject;

public final class UserDbHttpHandler implements HttpHandler {
    private final SqliteUserDataAccessObject userDao;
    private final String internalSecret;

    public UserDbHttpHandler(SqliteUserDataAccessObject userDao, String internalSecret) {
        this.userDao = Objects.requireNonNull(userDao, "userDao must not be null");
        this.internalSecret = DbHttpSupport.requireSecret(internalSecret);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DbHttpSupport.dispatch(exchange, internalSecret, (path, req) -> {
            switch (path) {
                case "/db/users/exists-by-username" -> {
                    boolean exists = userDao.existsByUsername(req.getString("username"));
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("exists", exists));
                }
                case "/db/users/exists-by-email" -> {
                    boolean exists = userDao.existsByEmail(req.getString("email"));
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("exists", exists));
                }
                case "/db/users/save" -> {
                    User user = new User(
                            req.getString("userId"),
                            req.getString("username"),
                            req.getString("email"),
                            req.getString("passwordHash"));
                    userDao.save(user);
                    DbHttpSupport.sendJson(exchange, 200, new JSONObject().put("ok", true));
                }
                case "/db/users/find-by-identifier" -> {
                    User user = userDao.findByIdentifier(req.getString("identifier"));
                    JSONObject res = new JSONObject();
                    if (user == null) {
                        res.put("user", JSONObject.NULL);
                    } else {
                        res.put("user", toJson(user));
                    }
                    DbHttpSupport.sendJson(exchange, 200, res);
                }
                default -> DbHttpSupport.sendError(exchange, 404, "Unknown endpoint: " + path);
            }
        });
    }

    private static JSONObject toJson(User user) {
        JSONObject obj = new JSONObject();
        obj.put("userId", user.getUserId());
        obj.put("username", user.getUsername());
        obj.put("email", user.getEmail());
        obj.put("passwordHash", user.getPasswordHash());
        return obj;
    }
}
