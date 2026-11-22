package csc207.mcp.frameworks_and_drivers.gateway;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.json.JSONException;
import org.json.JSONObject;

/** Shared HTTP validation, authentication, and JSON helpers for /db/ handlers. */
final class DbHttpSupport {
    static final String SECRET_HEADER = "X-Internal-Secret";

    private DbHttpSupport() {
    }

    @FunctionalInterface
    interface RouteAction {
        void handle(String path, JSONObject requestBody) throws IOException;
    }

    static String requireSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("internalSecret must not be blank");
        }
        return secret;
    }

    static void dispatch(HttpExchange exchange, String expectedSecret, RouteAction action)
            throws IOException {
        try {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            String providedSecret = exchange.getRequestHeaders().getFirst(SECRET_HEADER);
            if (!isSecretValid(providedSecret, expectedSecret)) {
                sendError(exchange, 401, "Unauthorized: invalid or missing internal secret");
                return;
            }

            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            if (contentType == null || !contentType.contains("application/json")) {
                sendError(exchange, 415, "Unsupported Media Type: application/json required");
                return;
            }

            String rawBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject requestBody;
            try {
                requestBody = new JSONObject(rawBody);
            } catch (JSONException e) {
                sendError(exchange, 400, "Invalid JSON request body");
                return;
            }

            String path = normalizePath(exchange.getRequestURI().getPath());
            action.handle(path, requestBody);
        } catch (JSONException e) {
            sendError(exchange, 400, "Missing or invalid JSON field: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendError(exchange, 409, e.getMessage() != null ? e.getMessage() : "Invalid request");
        } catch (Exception e) {
            sendError(exchange, 500, "Internal Server Error");
        }
    }

    static void sendJson(HttpExchange exchange, int statusCode, JSONObject body) throws IOException {
        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        JSONObject errorBody = new JSONObject();
        errorBody.put("error", message);
        sendJson(exchange, statusCode, errorBody);
    }

    private static boolean isSecretValid(String provided, String expected) {
        if (provided == null) {
            return false;
        }
        return MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }

    private static String normalizePath(String path) {
        if (path != null && path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}
