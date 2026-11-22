package csc207.mcp.frameworks_and_drivers.data_access_object;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.json.JSONException;
import org.json.JSONObject;

/** Shared HTTP client used by client-side Http*DataAccessObject implementations. */
public final class HttpDbClient {
    private static final String SECRET_HEADER = "X-Internal-Secret";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final String baseUrl;
    private final String internalSecret;
    private final HttpClient httpClient;

    public HttpDbClient(String baseUrl, String internalSecret) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be blank");
        }
        if (internalSecret == null || internalSecret.isBlank()) {
            throw new IllegalArgumentException("internalSecret must not be blank");
        }
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.internalSecret = internalSecret;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    /** Sends a POST JSON request and maps HTTP error statuses back to DAO exceptions. */
    public JSONObject post(String path, JSONObject requestBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json; charset=utf-8")
                .header(SECRET_HEADER, internalSecret)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling remote database endpoint: " + path, e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to reach remote database endpoint: " + path, e);
        }

        int status = response.statusCode();
        String body = response.body();
        if (status == 200) {
            try {
                return new JSONObject(body);
            } catch (JSONException e) {
                throw new IllegalStateException("Invalid JSON response from " + path, e);
            }
        }

        String errorMessage = extractErrorMessage(body, "HTTP " + status + " from " + path);
        if (status == 400 || status == 409) {
            throw new IllegalArgumentException(errorMessage);
        }
        throw new IllegalStateException(errorMessage);
    }

    private static String extractErrorMessage(String body, String fallback) {
        if (body == null || body.isBlank()) {
            return fallback;
        }
        try {
            JSONObject obj = new JSONObject(body);
            if (obj.has("error") && !obj.isNull("error")) {
                return obj.getString("error");
            }
        } catch (JSONException ignored) {
            // Fall back to default message when server body is not JSON.
        }
        return fallback;
    }
}
