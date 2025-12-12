package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.presentation.dto.MediaEntryDTO;
import at.fhtw.swen1.mrp.presentation.dto.RatingDTO;
import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.MediaService;
import at.fhtw.swen1.mrp.services.RatingService;
import at.fhtw.swen1.mrp.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class MediaController implements Controller  {
    private final MediaService mediaService;
    private final RatingService ratingService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public MediaController(MediaService mediaService, RatingService ratingService, TokenService tokenService) {
        this.mediaService = mediaService;
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

            // POST /api/media - Create media
            if (pathSize == 2 && request.getPathParts().get(1).equals("media")
                    && request.getMethod() == Method.POST) {

                return handleCreateMedia(request, userId.get());
            }

            // GET /api/media - Get all media
            if (pathSize == 2 && request.getPathParts().get(1).equals("media")
                    && request.getMethod() == Method.GET) {
                return handleGetAllMedia();
            }

            // GET /api/media/{id} - Get media by ID
            if (pathSize == 3 && request.getPathParts().get(1).equals("media")
                    && request.getMethod() == Method.GET) {
                String mediaId = request.getPathParts().get(2);
                return handleGetMediaById(mediaId);
            }

            // PUT /api/media/{id} - Update media
            if (pathSize == 3 && request.getPathParts().get(1).equals("media")
                    && request.getMethod() == Method.PUT) {

                String mediaId = request.getPathParts().get(2);
                return handleUpdateMedia(request, mediaId, userId.get());
            }

            // DELETE /api/media/{id} - Delete media
            if (pathSize == 3 && request.getPathParts().get(1).equals("media")
                    && request.getMethod() == Method.DELETE) {

                String mediaId = request.getPathParts().get(2);
                return handleDeleteMedia(mediaId, userId.get());
            }

            // POST /api/media/{id}/rate - Rate media
            if (pathSize == 4 && request.getPathParts().get(1).equals("media")
                    && request.getPathParts().get(3).equals("rate")
                    && request.getMethod() == Method.POST) {

                String mediaId = request.getPathParts().get(2);
                return handleRateMedia(request, mediaId, userId.get());
            }

            // GET /api/media/{id}/ratings - Get all ratings for media
            if (pathSize == 4 && request.getPathParts().get(1).equals("media")
                    && request.getPathParts().get(3).equals("ratings")
                    && request.getMethod() == Method.GET) {

                String mediaId = request.getPathParts().get(2);
                return handleGetMediaRatings(mediaId, userId.get());
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"error\": \"Endpoint not found\"}");

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SecurityException e) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }

    }



    private Response handleCreateMedia(Request request, UUID userId) {
        try {
            MediaEntryDTO dto = objectMapper.readValue(request.getBody(), MediaEntryDTO.class);

            if (!dto.isValid()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"error\": \"Invalid media data\"}");
            }

            MediaEntry mediaEntry = mediaService.createMedia(
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getMediaType(),
                    dto.getReleaseYear(),
                    dto.getGenres(),
                    dto.getAgeRestriction(),
                    userId
            );

            MediaEntryDTO response = new MediaEntryDTO(mediaEntry);

            return new Response(HttpStatus.CREATED, ContentType.JSON,
                    objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request body\"}");
        }
    }

    private Response handleGetAllMedia() {
        try {
            List<MediaEntry> mediaList = mediaService.getAllMedia();
            List<MediaEntryDTO> dtoList = mediaList.stream()
                    .map(MediaEntryDTO::new)
                    .toList();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    objectMapper.writeValueAsString(dtoList)
            );

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"Error retrieving media list\"}");
        }
    }

    private Response handleGetMediaById(String mediaIdStr) {
        try {
            UUID mediaId = UUID.fromString(mediaIdStr);
            MediaEntry mediaEntry = mediaService.getMediaById(mediaId);
            MediaEntryDTO response = new MediaEntryDTO(mediaEntry);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    objectMapper.writeValueAsString(response)
            );

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleUpdateMedia(Request request, String mediaIdStr, UUID userId) {
        try {
            UUID mediaId = UUID.fromString(mediaIdStr);
            MediaEntryDTO dto = objectMapper.readValue(request.getBody(), MediaEntryDTO.class);

            MediaEntry updatedMedia = mediaService.updateMedia(
                    mediaId,
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getMediaType(),
                    dto.getReleaseYear(),
                    dto.getGenres(),
                    dto.getAgeRestriction(),
                    userId
            );

            MediaEntryDTO response = new MediaEntryDTO(updatedMedia);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    objectMapper.writeValueAsString(response)
            );

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SecurityException e) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleDeleteMedia(String mediaIdStr, UUID userId) {
        try {
            UUID mediaId = UUID.fromString(mediaIdStr);
            mediaService.deleteMedia(mediaId, userId);

            return new Response(
                    HttpStatus.NO_CONTENT,
                    ContentType.JSON,
                    ""
            );

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SecurityException e) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response handleRateMedia(Request request, String mediaIdStr, UUID userId) {
        try {
            UUID mediaId = UUID.fromString(mediaIdStr);
            RatingDTO dto = objectMapper.readValue(request.getBody(), RatingDTO.class);

            Rating rating = ratingService.createRating(
                    mediaId,
                    userId,
                    dto.getStars(),
                    dto.getComment()
            );

            RatingDTO response = new RatingDTO(rating);

            return new Response(HttpStatus.CREATED, ContentType.JSON,
                    objectMapper.writeValueAsString(response));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }

    private Response handleGetMediaRatings(String mediaIdStr, UUID requestingUserId) {
        try {
            UUID mediaId = UUID.fromString(mediaIdStr);
            List<Rating> ratings = ratingService.getPublicRatingsForMedia(mediaId, requestingUserId);
            List<RatingDTO> dtoList = ratings.stream()
                    .map(RatingDTO::new)
                    .toList();

            return new Response(HttpStatus.OK, ContentType.JSON,
                    objectMapper.writeValueAsString(dtoList));

        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid media ID format\"}");
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
