package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.business.User;
import at.fhtw.swen1.mrp.presentation.dto.AuthResponse;
import at.fhtw.swen1.mrp.presentation.dto.MediaEntryDTO;
import at.fhtw.swen1.mrp.presentation.dto.RatingDTO;
import at.fhtw.swen1.mrp.presentation.dto.UserCredentialsRequest;
import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.FavoriteService;
import at.fhtw.swen1.mrp.services.RatingService;
import at.fhtw.swen1.mrp.services.TokenService;
import at.fhtw.swen1.mrp.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class UserController implements Controller {
    private final UserService userService;
    private final TokenService tokenService;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, TokenService tokenService, RatingService ratingService, FavoriteService favoriteService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            int pathSize = request.getPathParts().size();

            // /api/users/register
            if (pathSize == 3 && request.getPathParts().get(1).equals("users") &&
                    request.getPathParts().get(2).equals("register") &&
                    request.getMethod() == Method.POST) {
                return handleRegister(request);
            }

            // /api/users/login
            if (pathSize == 3 && request.getPathParts().get(1).equals("users") &&
                    request.getPathParts().get(2).equals("login") &&
                    request.getMethod() == Method.POST) {
                return handleLogin(request);
            }

            Optional<UUID> authenticatedUserId = validateToken(request);
            if (authenticatedUserId.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Unauthorized - Invalid or missing token\"}");
            }

            // GET /api/users/{id}/ratings - Get user's rating history
            if (pathSize == 4 && request.getPathParts().get(1).equals("users") &&
                    request.getPathParts().get(3).equals("ratings") &&
                    request.getMethod() == Method.GET) {
                String userId = request.getPathParts().get(2);
                return handleGetUserRatings(userId, authenticatedUserId.get());
            }

            // GET /api/users/{id}/favorites - Get user's favorites
            if (pathSize == 4 && request.getPathParts().get(1).equals("users") &&
                    request.getPathParts().get(3).equals("favorites") &&
                    request.getMethod() == Method.GET) {
                String userId = request.getPathParts().get(2);
                return handleGetUserFavorites(userId);
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"error\": \"Endpoint not found\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response handleRegister(Request request) {
        try {
            UserCredentialsRequest dto = objectMapper.readValue(request.getBody(), UserCredentialsRequest.class);

            if (!dto.isValid()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Invalid credentials\"}");
            }

            User user = userService.registerUser(dto.getUsername(), dto.getPassword());

            AuthResponse response = new AuthResponse(
                    "User registered successfully",
                    null,
                    user.getUsername()
            );

            return new Response(HttpStatus.CREATED, ContentType.JSON, objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request body\"}");
        }


    }

    private Response handleLogin(Request request) {
        try {
            UserCredentialsRequest dto = objectMapper.readValue(
                    request.getBody(), UserCredentialsRequest.class
            );

            if (!dto.isValid()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"error\": \"Invalid credentials\"}");
            }

            Optional<User> userOpt = userService.loginUser(dto.getUsername(), dto.getPassword());

            if (userOpt.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Invalid credentials\"}");
            }
            String token = tokenService.generateToken(userOpt.get().getUsername(), userOpt.get().getId());

            AuthResponse response = new AuthResponse(
                    "Login successful",
                    token,
                    userOpt.get().getUsername()
            );

            return new Response(HttpStatus.OK, ContentType.JSON, objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request body\"}");
        }
    }

    private Response handleGetUserRatings(String userIdStr, UUID requestingUserId) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            List<Rating> ratings = ratingService.getRatingsByUserId(userId, requestingUserId);
            List<RatingDTO> dtoList = ratings.stream()
                    .map(RatingDTO::new)
                    .toList();

            return new Response(HttpStatus.OK, ContentType.JSON,
                    objectMapper.writeValueAsString(dtoList));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid user ID format\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleGetUserFavorites(String userIdStr) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            List<MediaEntry> favorites = favoriteService.getFavoritesByUserId(userId);
            List<MediaEntryDTO> dtoList = favorites.stream()
                    .map(MediaEntryDTO::new)
                    .toList();

            return new Response(HttpStatus.OK, ContentType.JSON,
                    objectMapper.writeValueAsString(dtoList));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid user ID format\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Optional<UUID> validateToken(Request request) {
        String authHeader = request.getHeader("authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authHeader.substring(7);
        return tokenService.validateToken(token);
    }
}
