package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.entities.MediaEntry;
import at.fhtw.swen1.mrp.business.entities.Rating;
import at.fhtw.swen1.mrp.business.entities.User;
import at.fhtw.swen1.mrp.presentation.dto.AuthResponse;
import at.fhtw.swen1.mrp.presentation.dto.MediaEntryDTO;
import at.fhtw.swen1.mrp.presentation.dto.ProfileDTO;
import at.fhtw.swen1.mrp.presentation.dto.RatingDTO;
import at.fhtw.swen1.mrp.presentation.dto.UserCredentialsRequest;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.FavoriteService;
import at.fhtw.swen1.mrp.services.RatingService;
import at.fhtw.swen1.mrp.services.RecommendationService;
import at.fhtw.swen1.mrp.services.TokenService;
import at.fhtw.swen1.mrp.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserController extends BaseController {
    private final UserService userService;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;
    private final RecommendationService recommendationService;

    public UserController(UserService userService, TokenService tokenService, RatingService ratingService,
                          FavoriteService favoriteService, RecommendationService recommendationService) {
        super(tokenService);
        this.userService = userService;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
        this.recommendationService = recommendationService;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            // /api/users/register
            if (matchesRoute(request, Method.POST, 3, "register")) {
                return handleRegister(request);
            }

            // /api/users/login
            if (matchesRoute(request, Method.POST, 3, "login")) {
                return handleLogin(request);
            }

            // Protected endpoints
            Optional<UUID> userId = validateToken(request);
            if (userId.isEmpty()) {
                return unauthorized();
            }

            if (matchesRoute(request, Method.GET, 4, "ratings")) {
                return handleGetUserRatings(request, userId.get());
            }
            if (matchesRoute(request, Method.GET, 4, "favorites")) {
                return handleGetUserFavorites(request);
            }
            if (matchesRoute(request, Method.GET, 4, "profile")) {
                return handleGetProfile(request);
            }
            if (matchesRoute(request, Method.PUT, 4, "profile")) {
                return handleUpdateProfile(request, userId.get());
            }
            if (matchesRoute(request, Method.GET, 4, "recommendations")) {
                return handleGetRecommendations(request);
            }

            return notFound("Endpoint not found");

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private Response handleRegister(Request request) {
        UserCredentialsRequest dto = fromJson(request.getBody(), UserCredentialsRequest.class);

        if (!dto.isValid()) {
            return badRequest("Invalid credentials");
        }

        User user = userService.registerUser(dto.getUsername(), dto.getPassword());
        return message(HttpStatus.CREATED, "User: " + user.getUsername() + " registered successfully");
    }

    private Response handleLogin(Request request) {
        UserCredentialsRequest dto = fromJson(request.getBody(), UserCredentialsRequest.class);

        if (!dto.isValid()) {
            return badRequest("Invalid credentials");
        }

        Optional<User> userOpt = userService.loginUser(dto.getUsername(), dto.getPassword());

        if (userOpt.isEmpty()) {
            return unauthorized();
        }

        User user = userOpt.get();
        String token = tokenService.generateToken(user.getUsername(), user.getId());

        AuthResponse response = new AuthResponse("Login successful", token, user.getUsername());
        return ok(response);
    }

    private Response handleGetUserRatings(Request request, UUID requestingUserId) {
        UUID userId = getUUIDFromPath(request, 2);
        List<Rating> ratings = ratingService.getRatingsByUserId(userId, requestingUserId);

        List<RatingDTO> dtoList = ratings.stream()
                .map(RatingDTO::new)
                .toList();

        return ok(dtoList);
    }

    private Response handleGetUserFavorites(Request request) {
        UUID userId = getUUIDFromPath(request, 2);
        List<MediaEntry> favorites = favoriteService.getFavoritesByUserId(userId);

        List<MediaEntryDTO> dtoList = favorites.stream()
                .map(MediaEntryDTO::new)
                .toList();

        return ok(dtoList);
    }

    private Response handleGetProfile(Request request) {
        UUID userId = getUUIDFromPath(request, 2);
        Optional<User> userOpt = userService.getUserById(userId);

        if (userOpt.isEmpty()) {
            return notFound("User not found");
        }

        return ok(buildProfileDTO(userOpt.get()));
    }

    private Response handleUpdateProfile(Request request, UUID authenticatedUserId) {
        UUID userId = getUUIDFromPath(request, 2);

        if (!userId.equals(authenticatedUserId)) {
            return forbidden("You can only update your own profile");
        }

        ProfileDTO dto = fromJson(request.getBody(), ProfileDTO.class);
        User updatedUser = userService.updateProfile(userId, dto.getEmail(), dto.getFavoriteGenre());

        return ok(buildProfileDTO(updatedUser));
    }

    private Response handleGetRecommendations(Request request) {
        UUID userId = getUUIDFromPath(request, 2);
        String type = request.getQueryParam("type");

        List<MediaEntry> recommendations;
        if ("content".equalsIgnoreCase(type)) {
            recommendations = recommendationService.getContentBasedRecommendations(userId);
        } else {
            recommendations = recommendationService.getGenreBasedRecommendations(userId);
        }

        List<MediaEntryDTO> dtoList = recommendations.stream()
                .map(MediaEntryDTO::new)
                .toList();

        return ok(dtoList);
    }

    private ProfileDTO buildProfileDTO(User user) {
        ProfileDTO profile = new ProfileDTO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setFavoriteGenre(user.getFavoriteGenre());
        profile.setTotalRatings(ratingService.getTotalRatingsForUser(user.getId()));
        profile.setAverageScore(ratingService.getAverageScoreForUser(user.getId()));
        return profile;
    }

}