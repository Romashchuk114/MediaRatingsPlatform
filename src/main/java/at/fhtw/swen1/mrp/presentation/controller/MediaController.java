package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.presentation.dto.MediaEntryDTO;
import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.MediaService;
import at.fhtw.swen1.mrp.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;


public class MediaController implements Controller  {
    private final MediaService mediaService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public MediaController(MediaService mediaService, TokenService tokenService) {
        this.mediaService = mediaService;
        this.tokenService = tokenService;
        this.objectMapper = new ObjectMapper();
    }
    @Override
    public Response handleRequest(Request request) {

            // POST /api/media - Create media
            if ("media".equals(request.getPathParts().get(1)) &&
                    request.getMethod() == Method.POST) {

                Optional<UUID> userId = validateToken(request);
                if (userId.isEmpty()) {
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                            "{\"error\": \"Unauthorized - Invalid or missing token\"}");
                }

                return handleCreateMedia(request, userId.get());
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"error\": \"Endpoint not found\"}");
    }

    private Optional<UUID> validateToken(Request request) {
        String authHeader = request.getHeader("authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authHeader.substring(7);
        return tokenService.validateToken(token);
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

            MediaEntryDTO response = new MediaEntryDTO(
                    mediaEntry.getId(),
                    mediaEntry.getTitle(),
                    mediaEntry.getDescription(),
                    mediaEntry.getMediaType().name(),
                    mediaEntry.getReleaseYear(),
                    mediaEntry.getGenres(),
                    mediaEntry.getAgeRestriction(),
                    mediaEntry.getAverageScore(),
                    mediaEntry.getCreatorId()
            );

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

}
