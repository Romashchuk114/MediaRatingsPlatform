package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseController implements Controller {
    protected final TokenService tokenService;
    protected final ObjectMapper objectMapper;

    protected BaseController(TokenService tokenService) {
        this.tokenService = tokenService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    protected <T> Response ok(T data) {
        return sendJson(HttpStatus.OK, data);
    }

    protected <T> Response created(T data) {
        return sendJson(HttpStatus.CREATED, data);
    }

    protected Response noContent() {
        return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "");
    }

    protected Response error(HttpStatus status, String message) {
        return new Response(status, ContentType.JSON, "{\"error\": \"" + message + "\"}");
    }

    protected Response unauthorized() {
        return error(HttpStatus.UNAUTHORIZED, "Unauthorized - Invalid or missing token");
    }

    protected Response notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    protected Response badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    protected Response forbidden(String message) {
        return error(HttpStatus.FORBIDDEN, message);
    }

    protected Response message(HttpStatus status, String message) {
        return new Response(status, ContentType.JSON, "{\"message\": \"" + message + "\"}");
    }

    protected <T> Response sendJson(HttpStatus status, T data) {
        try {
            String jsonContent = objectMapper.writeValueAsString(data);
            return new Response(status, ContentType.JSON, jsonContent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing response", e);
        }
    }

    protected Optional<UUID> validateToken(Request request) {
        if (tokenService == null) {
            return Optional.empty();
        }

        String authHeader = request.getHeader("authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authHeader.substring(7);
        return tokenService.validateToken(token);
    }

    protected boolean matchesRoute(Request request, Method method, int pathSize) {
        return request.getMethod() == method && request.getPathParts().size() == pathSize;
    }

    protected boolean matchesRoute(Request request, Method method, int pathSize, String lastPart) {
        if (!matchesRoute(request, method, pathSize)) {
            return false;
        }
        return getLastPathPart(request).equals(lastPart);
    }

    protected String getLastPathPart(Request request) {
        List<String> parts = request.getPathParts();
        return parts.isEmpty() ? "" : parts.get(parts.size() - 1);
    }

    protected UUID getUUIDFromPath(Request request, int index) {
        return UUID.fromString(request.getPathParts().get(index));
    }

    protected <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }

    protected Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
