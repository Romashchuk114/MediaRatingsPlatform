package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.data.FavoriteRepository;
import at.fhtw.swen1.mrp.data.MediaRepository;

import java.util.List;
import java.util.UUID;

public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final MediaRepository mediaRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, MediaRepository mediaRepository) {
        this.favoriteRepository = favoriteRepository;
        this.mediaRepository = mediaRepository;
    }

    public void addFavorite(UUID userId, UUID mediaId) {
        if (!mediaRepository.existsById(mediaId)) {
            throw new IllegalArgumentException("Media entry not found");
        }

        if (favoriteRepository.isFavorite(userId, mediaId)) {
            throw new IllegalArgumentException("Media is already in favorites");
        }

        favoriteRepository.addFavorite(userId, mediaId);
    }

    public void removeFavorite(UUID userId, UUID mediaId) {
        if (!favoriteRepository.isFavorite(userId, mediaId)) {
            throw new IllegalArgumentException("Media is not in favorites");
        }

        favoriteRepository.removeFavorite(userId, mediaId);
    }

    public List<MediaEntry> getFavoritesByUserId(UUID userId) {
        return favoriteRepository.findFavoritesByUserId(userId);
    }
}
