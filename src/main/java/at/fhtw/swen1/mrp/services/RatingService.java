package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.data.MediaRepository;
import at.fhtw.swen1.mrp.data.RatingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RatingService {
    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository;

    public RatingService(RatingRepository ratingRepository, MediaRepository mediaRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
    }

    public Rating createRating(UUID mediaId, UUID userId, int stars, String comment) {
        if (!mediaRepository.existsById(mediaId)) {
            throw new IllegalArgumentException("Media entry not found");
        }

        Optional<Rating> existingRating = ratingRepository.findByMediaIdAndUserId(mediaId, userId);
        if (existingRating.isPresent()) {
            throw new IllegalArgumentException("User has already rated this media");
        }

        Rating rating = new Rating(mediaId, userId, stars, comment);
        Rating savedRating = ratingRepository.save(rating);

        updateAverageScore(mediaId);

        return savedRating;
    }

    public Rating updateRating(UUID ratingId, UUID userId, int stars, String comment) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }

        Rating rating = ratingOpt.get();
        if (!rating.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this rating");
        }

        rating.setStars(stars);
        rating.setComment(comment);
        rating.setUpdatedAt(LocalDateTime.now());

        Rating updatedRating = ratingRepository.save(rating);

        updateAverageScore(rating.getMediaId());

        return updatedRating;
    }

    public Optional<Rating> deleteRating(UUID ratingId, UUID userId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isEmpty()) {
            return Optional.empty();
        }

        Rating rating = ratingOpt.get();
        if (!rating.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this rating");
        }

        UUID mediaId = rating.getMediaId();
        Optional<Rating> deletedRating = ratingRepository.delete(ratingId);

        updateAverageScore(mediaId);

        return deletedRating;
    }

    public Rating setPublic(UUID ratingId, UUID userId, boolean isPublic) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }

        Rating rating = ratingOpt.get();
        if (!rating.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this rating");
        }

        rating.setPublic(isPublic);
        rating.setUpdatedAt(LocalDateTime.now());

        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsByUserId(UUID userId, UUID requestingUserId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);

        for (Rating rating : ratings) {
            if (!rating.isPublic() && !rating.getUserId().equals(requestingUserId)) {
                rating.setComment(null);
            }
        }

        return ratings;
    }

    public void likeRating(UUID ratingId, UUID userId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }

        Rating rating = ratingOpt.get();
        if (rating.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cannot like your own rating");
        }

        if (ratingRepository.hasUserLiked(ratingId, userId)) {
            throw new IllegalArgumentException("You have already liked this rating");
        }

        ratingRepository.addLike(ratingId, userId);
    }

    public void unlikeRating(UUID ratingId, UUID userId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isEmpty()) {
            throw new IllegalArgumentException("Rating not found");
        }

        if (!ratingRepository.hasUserLiked(ratingId, userId)) {
            throw new IllegalArgumentException("You have not liked this rating");
        }

        ratingRepository.removeLike(ratingId, userId);
    }

    public List<Rating> getPublicRatingsForMedia(UUID mediaId, UUID requestingUserId) {
        List<Rating> allRatings = ratingRepository.findByMediaId(mediaId);

        for (Rating rating : allRatings) {
            if (!rating.isPublic() && !rating.getUserId().equals(requestingUserId)) {
                rating.setComment(null);
            }
        }

        return allRatings;
    }

    private void updateAverageScore(UUID mediaId) {
        List<Rating> ratings = ratingRepository.findByMediaId(mediaId);

        double averageScore = 0.0;
        if (!ratings.isEmpty()) {
            int totalStars = 0;
            for (Rating rating : ratings) {
                totalStars += rating.getStars();
            }
            averageScore = Math.round(((double) totalStars / ratings.size()) * 100.0) / 100.0;
        }

        Optional<MediaEntry> mediaOpt = mediaRepository.findById(mediaId);
        if (mediaOpt.isPresent()) {
            MediaEntry media = mediaOpt.get();
            media.setAverageScore(averageScore);
            mediaRepository.save(media);
        }
    }

    public int getTotalRatingsForUser(UUID userId) {
        return ratingRepository.findByUserId(userId).size();
    }

    public double getAverageScoreForUser(UUID userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        if (ratings.isEmpty()) {
            return 0.0;
        }

        int totalStars = 0;
        for (Rating rating : ratings) {
            totalStars += rating.getStars();
        }
        return Math.round(((double) totalStars / ratings.size()) * 100.0) / 100.0;
    }
}
