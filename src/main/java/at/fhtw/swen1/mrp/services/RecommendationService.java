package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.data.MediaRepository;
import at.fhtw.swen1.mrp.data.RatingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RecommendationService {
    private final RatingRepository ratingRepository;
    private final MediaRepository mediaRepository;

    public RecommendationService(RatingRepository ratingRepository, MediaRepository mediaRepository) {
        this.ratingRepository = ratingRepository;
        this.mediaRepository = mediaRepository;
    }

    public List<MediaEntry> getGenreBasedRecommendations(UUID userId) {
        List<Rating> userRatings = ratingRepository.findByUserId(userId);
        List<UUID> ratedMediaIds = new ArrayList<>();
        List<String> preferredGenres = new ArrayList<>();

        for (Rating rating : userRatings) {
            ratedMediaIds.add(rating.getMediaId());

            if (rating.getStars() >= 4) {
                Optional<MediaEntry> mediaOpt = mediaRepository.findById(rating.getMediaId());
                if (mediaOpt.isPresent()) {
                    for (String genre : mediaOpt.get().getGenres()) {
                        if (!preferredGenres.contains(genre)) {
                            preferredGenres.add(genre);
                        }
                    }
                }
            }
        }

        if (preferredGenres.isEmpty()) {
            return new ArrayList<>();
        }

        List<MediaEntry> allMedia = mediaRepository.findAll();
        List<MediaEntry> recommendations = new ArrayList<>();

        for (MediaEntry media : allMedia) {
            if (!ratedMediaIds.contains(media.getId())) {
                for (String genre : media.getGenres()) {
                    if (preferredGenres.contains(genre)) {
                        recommendations.add(media);
                        break;
                    }
                }
            }
        }

        return recommendations;
    }

    public List<MediaEntry> getContentBasedRecommendations(UUID userId) {
        List<Rating> userRatings = ratingRepository.findByUserId(userId);
        List<UUID> ratedMediaIds = new ArrayList<>();
        List<MediaEntry> highlyRatedMedia = new ArrayList<>();

        for (Rating rating : userRatings) {
            ratedMediaIds.add(rating.getMediaId());

            if (rating.getStars() >= 4) {
                Optional<MediaEntry> mediaOpt = mediaRepository.findById(rating.getMediaId());
                mediaOpt.ifPresent(highlyRatedMedia::add);
            }
        }

        if (highlyRatedMedia.isEmpty()) {
            return new ArrayList<>();
        }

        List<MediaEntry> allMedia = mediaRepository.findAll();
        List<MediaEntry> recommendations = new ArrayList<>();

        for (MediaEntry candidate : allMedia) {
            if (!ratedMediaIds.contains(candidate.getId())) {
                for (MediaEntry liked : highlyRatedMedia) {
                    if (isContentSimilar(candidate, liked)) {
                        recommendations.add(candidate);
                        break;
                    }
                }
            }
        }

        return recommendations;
    }

    private boolean isContentSimilar(MediaEntry candidate, MediaEntry reference) {
        boolean sameMediaType = candidate.getMediaType() == reference.getMediaType();
        boolean sameAgeRestriction = candidate.getAgeRestriction() == reference.getAgeRestriction();
        boolean hasMatchingGenre = false;

        for (String genre : candidate.getGenres()) {
            if (reference.getGenres().contains(genre)) {
                hasMatchingGenre = true;
                break;
            }
        }

        return sameMediaType && sameAgeRestriction && hasMatchingGenre;
    }
}