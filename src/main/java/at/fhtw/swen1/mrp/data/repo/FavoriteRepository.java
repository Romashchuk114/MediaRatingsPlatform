package at.fhtw.swen1.mrp.data.repo;

import at.fhtw.swen1.mrp.business.entities.MediaEntry;
import java.util.List;
import java.util.UUID;

public interface FavoriteRepository {
    void addFavorite(UUID userId, UUID mediaId);

    void removeFavorite(UUID userId, UUID mediaId);

    boolean isFavorite(UUID userId, UUID mediaId);

    List<MediaEntry> findFavoritesByUserId(UUID userId);
}
