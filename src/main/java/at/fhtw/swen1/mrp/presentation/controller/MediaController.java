package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.presentation.dto.MediaEntryDTO;
import at.fhtw.swen1.mrp.presentation.dto.RatingDTO;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.FavoriteService;
import at.fhtw.swen1.mrp.services.MediaService;
import at.fhtw.swen1.mrp.services.RatingService;
import at.fhtw.swen1.mrp.services.TokenService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaController extends BaseController {
    private final MediaService mediaService;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;

    public MediaController(MediaService mediaService, RatingService ratingService, FavoriteService favoriteService,
                           TokenService tokenService) {
        super(tokenService);
        this.mediaService = mediaService;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
    }

    @Override
    public Response handleRequest(Request request) {
        Optional<UUID> userId = validateToken(request);
        if (userId.isEmpty()) {
            return unauthorized();
        }

        try {
            // /api/media
            if (matchesRoute(request, Method.POST, 2)) return handleCreateMedia(request, userId.get());
            if (matchesRoute(request, Method.GET, 2)) return handleGetAllMediaOrSearch(request);

            // /api/media/{id}
            if (matchesRoute(request, Method.GET, 3)) return handleGetMediaById(request);
            if (matchesRoute(request, Method.PUT, 3)) return handleUpdateMedia(request, userId.get());
            if (matchesRoute(request, Method.DELETE, 3)) return handleDeleteMedia(request, userId.get());

            // /api/media/{id}/rate
            if (matchesRoute(request, Method.POST, 4, "rate")) return handleRateMedia(request, userId.get());

            // /api/media/{id}/ratings
            if (matchesRoute(request, Method.GET, 4, "ratings")) return handleGetMediaRatings(request, userId.get());

            // /api/media/{id}/favorite
            if (matchesRoute(request, Method.POST, 4, "favorite")) return handleAddFavorite(request, userId.get());
            if (matchesRoute(request, Method.DELETE, 4, "favorite")) return handleRemoveFavorite(request, userId.get());

            return notFound("Endpoint not found");

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (SecurityException e) {
            return forbidden(e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private Response handleCreateMedia(Request request, UUID userId) {
        MediaEntryDTO dto = fromJson(request.getBody(), MediaEntryDTO.class);

        if (!dto.isValid()) {
            return badRequest("Invalid media data");
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

        return created(new MediaEntryDTO(mediaEntry));
    }

    private Response handleGetAllMediaOrSearch(Request request) {
        String title = request.getQueryParam("title");
        String genre = request.getQueryParam("genre");
        String mediaType = request.getQueryParam("mediaType");
        String sortBy = request.getQueryParam("sortBy");
        String sortOrder = request.getQueryParam("sortOrder");
        Integer releaseYear = parseInteger(request.getQueryParam("releaseYear"));
        Integer ageRestriction = parseInteger(request.getQueryParam("ageRestriction"));
        Double rating = parseDouble(request.getQueryParam("rating"));

        List<MediaEntry> results = mediaService.searchMedia(
                title, genre, mediaType, releaseYear, ageRestriction, rating, sortBy, sortOrder
        );

        List<MediaEntryDTO> dtoList = results.stream()
                .map(MediaEntryDTO::new)
                .toList();

        return ok(dtoList);
    }

    private Response handleGetMediaById(Request request) {
        UUID mediaId = getUUIDFromPath(request, 2);
        MediaEntry mediaEntry = mediaService.getMediaById(mediaId);
        return ok(new MediaEntryDTO(mediaEntry));
    }

    private Response handleUpdateMedia(Request request, UUID userId) {
        UUID mediaId = getUUIDFromPath(request, 2);
        MediaEntryDTO dto = fromJson(request.getBody(), MediaEntryDTO.class);

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

        return ok(new MediaEntryDTO(updatedMedia));
    }

    private Response handleDeleteMedia(Request request, UUID userId) {
        UUID mediaId = getUUIDFromPath(request, 2);
        mediaService.deleteMedia(mediaId, userId);
        return noContent();
    }

    private Response handleRateMedia(Request request, UUID userId) {
        UUID mediaId = getUUIDFromPath(request, 2);
        RatingDTO dto = fromJson(request.getBody(), RatingDTO.class);

        Rating rating = ratingService.createRating(mediaId, userId, dto.getStars(), dto.getComment());
        return created(new RatingDTO(rating));
    }

    private Response handleGetMediaRatings(Request request, UUID requestingUserId) {
        UUID mediaId = getUUIDFromPath(request, 2);
        List<Rating> ratings = ratingService.getPublicRatingsForMedia(mediaId, requestingUserId);

        List<RatingDTO> dtoList = ratings.stream()
                .map(RatingDTO::new)
                .toList();

        return ok(dtoList);
    }

    private Response handleAddFavorite(Request request, UUID userId) {
        UUID mediaId = getUUIDFromPath(request, 2);
        favoriteService.addFavorite(userId, mediaId);
        return message(HttpStatus.OK, "Media added to favorites");
    }

    private Response handleRemoveFavorite(Request request, UUID userId) {
        UUID mediaId = getUUIDFromPath(request, 2);
        favoriteService.removeFavorite(userId, mediaId);
        return message(HttpStatus.OK, "Media removed from favorites");
    }
}
