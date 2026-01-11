package at.fhtw.swen1.mrp.data.repo;

import at.fhtw.swen1.mrp.business.entities.Rating;
import at.fhtw.swen1.mrp.business.transfer.UserRatingCount;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface RatingRepository extends Repository<Rating> {
    List<Rating> findByMediaId(UUID mediaId);

    Optional<Rating> findByMediaIdAndUserId(UUID mediaId, UUID userId);

    List<Rating> findByUserId(UUID userId);

    void addLike(UUID ratingId, UUID userId);

    void removeLike(UUID ratingId, UUID userId);

    boolean hasUserLiked(UUID ratingId, UUID userId);

    List<UserRatingCount> getRatingCountsPerUser();
}


