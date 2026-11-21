package csc207.mcp.frameworks_and_drivers.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import csc207.mcp.interface_adapter.invoke.GatewayHandler;
import csc207.mcp.interface_adapter.invoke.InvokeResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.json.JSONObject;

public class McpGatewayServer {
    private final HttpServer server;
    private final GatewayHandler handler;
    private final Semaphore semaphore;

    public McpGatewayServer(GatewayHandler handler, int port, int maxConcurrent) throws IOException {
        this.handler = handler;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.semaphore = new Semaphore(maxConcurrent);
        this.server.createContext("/mcp", this::handleRequest);
        this.server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        if (!semaphore.tryAcquire()) {
            sendResponse(exchange, 503, "Server busy");
            return;
        }
        try {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method not allowed");
                return;
            }

            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");

            String apiKey = extractApiKey(authHeader);
            if (apiKey == null) {
                sendResponse(exchange, 401, "Unauthorized: missing or empty API key");
                return;
            }

            if (contentType == null || !contentType.contains("application/json")) {
                sendResponse(exchange, 415, "Unsupported Media Type: application/json required");
                return;
            }

            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                InvokeResponse response = handler.handleRequest(apiKey, body);
                sendResponse(exchange, Integer.parseInt(response.getStatusCode()), response.getResponse());
            } catch (Exception e) {
                sendResponse(exchange, 500, "Internal Server Error");
            }
        } finally {
            semaphore.release();
        }

    }

    private String extractApiKey(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }
        String apiKey = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7).trim()
                : authHeader.trim();
        return apiKey.isEmpty() ? null : apiKey;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String jsonString;
        if (statusCode >= 400) {
            JSONObject body = new JSONObject();
            body.put("error", message);
            jsonString = body.toString();
        } else {
            jsonString = message;
        }

        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        OutputStream os = exchange.getResponseBody();
        try {
            os.write(bytes);
        } finally {
            os.close();
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
