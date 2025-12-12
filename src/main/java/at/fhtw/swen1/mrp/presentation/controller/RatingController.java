package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.presentation.dto.RatingDTO;
import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.RatingService;
import at.fhtw.swen1.mrp.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Optional;
import java.util.UUID;

public class RatingController implements Controller {
    private final RatingService ratingService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public RatingController(RatingService ratingService, TokenService tokenService) {
        this.ratingService = ratingService;
        this.tokenService = tokenService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            int pathSize = request.getPathParts().size();

            Optional<UUID> userId = validateToken(request);
            if (userId.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Unauthorized - Invalid or missing token\"}");
            }

            // PUT /api/ratings/{id} - Update rating
            if (pathSize == 3 && request.getPathParts().get(1).equals("ratings")
                    && request.getMethod() == Method.PUT) {
                String ratingId = request.getPathParts().get(2);
                return handleUpdateRating(request, ratingId, userId.get());
            }

            // POST /api/ratings/{id}/like - Like rating
            if (pathSize == 4 && request.getPathParts().get(1).equals("ratings")
                    && request.getPathParts().get(3).equals("like")
                    && request.getMethod() == Method.POST) {
                String ratingId = request.getPathParts().get(2);
                return handleLikeRating(ratingId, userId.get());
            }

            // DELETE /api/ratings/{id}/like - Unlike rating
            if (pathSize == 4 && request.getPathParts().get(1).equals("ratings")
                    && request.getPathParts().get(3).equals("like")
                    && request.getMethod() == Method.DELETE) {
                String ratingId = request.getPathParts().get(2);
                return handleUnlikeRating(ratingId, userId.get());
            }

            // POST /api/ratings/{id}/confirm - Confirm rating (make public)
            if (pathSize == 4 && request.getPathParts().get(1).equals("ratings")
                    && request.getPathParts().get(3).equals("confirm")
                    && request.getMethod() == Method.POST) {
                String ratingId = request.getPathParts().get(2);
                return handleConfirmRating(ratingId, userId.get());
            }

            // DELETE /api/ratings/{id} - Delete rating
            if (pathSize == 3 && request.getPathParts().get(1).equals("ratings")
                    && request.getMethod() == Method.DELETE) {
                String ratingId = request.getPathParts().get(2);
                return handleDeleteRating(ratingId, userId.get());
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"error\": \"Endpoint not found\"}");

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleUpdateRating(Request request, String ratingIdStr, UUID userId) {
        try {
            UUID ratingId = UUID.fromString(ratingIdStr);
            RatingDTO dto = objectMapper.readValue(request.getBody(), RatingDTO.class);

            Rating updatedRating = ratingService.updateRating(
                    ratingId,
                    userId,
                    dto.getStars(),
                    dto.getComment()
            );

            RatingDTO response = new RatingDTO(updatedRating);

            return new Response(HttpStatus.OK, ContentType.JSON,
                    objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleLikeRating(String ratingIdStr, UUID userId) {
        try {
            UUID ratingId = UUID.fromString(ratingIdStr);
            ratingService.likeRating(ratingId, userId);

            return new Response(HttpStatus.OK, ContentType.JSON,
                    "{\"message\": \"Rating liked successfully\"}");

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleUnlikeRating(String ratingIdStr, UUID userId) {
        try {
            UUID ratingId = UUID.fromString(ratingIdStr);
            ratingService.unlikeRating(ratingId, userId);

            return new Response(HttpStatus.OK, ContentType.JSON,
                    "{\"message\": \"Rating unliked successfully\"}");

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleConfirmRating(String ratingIdStr, UUID userId) {
        try {
            UUID ratingId = UUID.fromString(ratingIdStr);
            Rating confirmedRating = ratingService.setPublic(ratingId, userId, true);

            RatingDTO response = new RatingDTO(confirmedRating);

            return new Response(HttpStatus.OK, ContentType.JSON,
                    objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleDeleteRating(String ratingIdStr, UUID userId) {
        try {
            UUID ratingId = UUID.fromString(ratingIdStr);
            Optional<Rating> deletedRating = ratingService.deleteRating(ratingId, userId);

            if (deletedRating.isEmpty()) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                        "{\"error\": \"Rating not found\"}");
            }

            RatingDTO response = new RatingDTO(deletedRating.get());

            return new Response(HttpStatus.OK, ContentType.JSON,
                    objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
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
