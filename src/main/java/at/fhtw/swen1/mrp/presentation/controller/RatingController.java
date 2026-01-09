package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.presentation.dto.RatingDTO;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.RatingService;
import at.fhtw.swen1.mrp.services.TokenService;

import java.util.Optional;
import java.util.UUID;

public class RatingController extends BaseController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService, TokenService tokenService) {
        super(tokenService);
        this.ratingService = ratingService;
    }

    @Override
    public Response handleRequest(Request request) {
        Optional<UUID> userId = validateToken(request);
        if (userId.isEmpty()) {
            return unauthorized();
        }

        try {
            // /api/ratings/{id}
            if (matchesRoute(request, Method.PUT, 3)) return handleUpdateRating(request, userId.get());
            if (matchesRoute(request, Method.DELETE, 3)) return handleDeleteRating(request, userId.get());

            // /api/ratings/{id}/like
            if (matchesRoute(request, Method.POST, 4, "like")) return handleLikeRating(request, userId.get());
            if (matchesRoute(request, Method.DELETE, 4, "like")) return handleUnlikeRating(request, userId.get());

            // /api/ratings/{id}/confirm
            if (matchesRoute(request, Method.POST, 4, "confirm")) return handleConfirmRating(request, userId.get());

            return notFound("Endpoint not found");

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (SecurityException e) {
            return forbidden(e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private Response handleUpdateRating(Request request, UUID userId) {
        UUID ratingId = getUUIDFromPath(request, 2);
        RatingDTO dto = fromJson(request.getBody(), RatingDTO.class);

        Rating updatedRating = ratingService.updateRating(ratingId, userId, dto.getStars(), dto.getComment());
        return ok(new RatingDTO(updatedRating));
    }

    private Response handleDeleteRating(Request request, UUID userId) {
        UUID ratingId = getUUIDFromPath(request, 2);
        Optional<Rating> deletedRating = ratingService.deleteRating(ratingId, userId);

        if (deletedRating.isEmpty()) {
            return notFound("Rating not found");
        }

        return ok(new RatingDTO(deletedRating.get()));
    }

    private Response handleLikeRating(Request request, UUID userId) {
        UUID ratingId = getUUIDFromPath(request, 2);
        ratingService.likeRating(ratingId, userId);
        return message(HttpStatus.OK, "Rating liked successfully");
    }

    private Response handleUnlikeRating(Request request, UUID userId) {
        UUID ratingId = getUUIDFromPath(request, 2);
        ratingService.unlikeRating(ratingId, userId);
        return message(HttpStatus.OK, "Rating unliked successfully");
    }

    private Response handleConfirmRating(Request request, UUID userId) {
        UUID ratingId = getUUIDFromPath(request, 2);
        Rating confirmedRating = ratingService.setPublic(ratingId, userId, true);
        return ok(new RatingDTO(confirmedRating));
    }
}