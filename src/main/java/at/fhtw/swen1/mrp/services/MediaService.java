package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.MediaType;
import at.fhtw.swen1.mrp.business.User;
import at.fhtw.swen1.mrp.data.MediaRepository;
import at.fhtw.swen1.mrp.data.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaService {
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    public MediaService(MediaRepository mediaRepository, UserRepository userRepository) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    public MediaEntry createMedia(String title, String description, String mediaTypeStr,
                                  int releaseYear, List<String> genres, int ageRestriction,
                                  UUID creatorId) {

        validateMediaInput(title, mediaTypeStr, genres, releaseYear, ageRestriction);

        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(mediaTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid media type. Must be: movie, series, or game");
        }

        Optional<User> creatorOpt = userRepository.findById(creatorId);
        if (creatorOpt.isEmpty()) {
            throw new IllegalArgumentException("Creator not found");
        }

        MediaEntry mediaEntry = new MediaEntry(
                title,
                description,
                mediaType,
                releaseYear,
                ageRestriction,
                genres,
                creatorId
        );

        return mediaRepository.save(mediaEntry);
    }

    public MediaEntry getMediaById(UUID id) {
        Optional<MediaEntry> mediaOpt = mediaRepository.findById(id);
        if (mediaOpt.isEmpty()) {
            throw new IllegalArgumentException("Media with ID '" + id + "' not found");
        }
        return mediaOpt.get();
    }

    public List<MediaEntry> getAllMedia() {
        return mediaRepository.findAll();
    }

    public List<MediaEntry> searchMedia(String title, String genre, String mediaType,
                                        Integer releaseYear, Integer ageRestriction, Double rating,
                                        String sortBy, String sortOrder) {
        return mediaRepository.search(title, genre, mediaType, releaseYear, ageRestriction, rating, sortBy, sortOrder);
    }

    public MediaEntry updateMedia(UUID mediaId, String title, String description,
                                  String mediaTypeStr, int releaseYear,
                                  List<String> genres, int ageRestriction,
                                  UUID userId) {

        MediaEntry existingMedia = getMediaById(mediaId);

        if (!existingMedia.getCreatorId().equals(userId)) {
            throw new SecurityException("Only the creator can update this media entry");
        }

        validateMediaInput(title, mediaTypeStr, genres, releaseYear, ageRestriction);

        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(mediaTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid media type. Must be: movie, series, or game");
        }

        existingMedia.setTitle(title);
        existingMedia.setDescription(description);
        existingMedia.setMediaType(mediaType);
        existingMedia.setReleaseYear(releaseYear);
        existingMedia.setGenres(genres);
        existingMedia.setAgeRestriction(ageRestriction);

        return mediaRepository.save(existingMedia);
    }

    public void deleteMedia(UUID mediaId, UUID userId) {
        MediaEntry existingMedia = getMediaById(mediaId);

        if (!existingMedia.getCreatorId().equals(userId)) {
            throw new SecurityException("Only the creator can delete this media entry");
        }

        mediaRepository.delete(mediaId);
    }

    private void validateMediaInput(String title, String mediaTypeStr,
                                    List<String> genres, int releaseYear,
                                    int ageRestriction) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (mediaTypeStr == null || mediaTypeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Media type cannot be empty");
        }

        if (genres == null || genres.isEmpty()) {
            throw new IllegalArgumentException("Genres cannot be empty");
        }

        if (releaseYear <= 0) {
            throw new IllegalArgumentException("Release year must be positive");
        }

        if (ageRestriction < 0) {
            throw new IllegalArgumentException("Age restriction cannot be negative");
        }
    }
}
