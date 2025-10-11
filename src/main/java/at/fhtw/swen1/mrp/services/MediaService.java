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
            throw new IllegalArgumentException("Age restriction must be positive");
        }

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


}
