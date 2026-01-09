package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.MediaType;
import at.fhtw.swen1.mrp.business.Rating;
import at.fhtw.swen1.mrp.data.MediaRepository;
import at.fhtw.swen1.mrp.data.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MediaRepository mediaRepository;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(ratingRepository, mediaRepository);
    }

    @Test
    void getGenreBasedRecommendations_shouldReturnMediaWithMatchingGenres() {
        UUID userId = UUID.randomUUID();
        UUID ratedMediaId = UUID.randomUUID();
        UUID recommendedMediaId = UUID.randomUUID();
        UUID anotherMediaId = UUID.randomUUID();

        MediaEntry ratedMedia = new MediaEntry(ratedMediaId, "Inception", "Description",
            MediaType.MOVIE, 2010, 12, 5.0, List.of("sci-fi", "thriller"), userId);
        MediaEntry recommendedMedia = new MediaEntry(recommendedMediaId, "Interstellar", "Description",
            MediaType.MOVIE, 2014, 12, 4.5, List.of("sci-fi", "drama"), userId);
        MediaEntry anotherMedia = new MediaEntry(anotherMediaId, "Shutter Island", "Description",
                MediaType.MOVIE, 2010, 12, 4, List.of("action", "drama"), userId);

        Rating highRating = new Rating(ratedMediaId, userId, 5, "Super!");

        when(ratingRepository.findByUserId(userId)).thenReturn(List.of(highRating));
        when(mediaRepository.findById(ratedMediaId)).thenReturn(Optional.of(ratedMedia));
        when(mediaRepository.findAll()).thenReturn(List.of(ratedMedia, recommendedMedia, anotherMedia));

        List<MediaEntry> result = recommendationService.getGenreBasedRecommendations(userId);

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.getFirst().getTitle());
    }

    @Test
    void getGenreBasedRecommendations_withNoHighRatings_shouldReturnEmpty() {
        UUID userId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();

        Rating lowRating = new Rating(mediaId, userId, 2, "bad");

        when(ratingRepository.findByUserId(userId)).thenReturn(List.of(lowRating));

        List<MediaEntry> result = recommendationService.getGenreBasedRecommendations(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGenreBasedRecommendations_shouldExcludeAlreadyRatedMedia() {
        UUID userId = UUID.randomUUID();
        UUID ratedMediaId = UUID.randomUUID();

        MediaEntry ratedMedia = new MediaEntry(ratedMediaId, "Inception", "Description",
            MediaType.MOVIE, 2010, 12, 5.0, List.of("sci-fi", "thriller"), userId);

        Rating highRating = new Rating(ratedMediaId, userId, 5, "Super!");

        when(ratingRepository.findByUserId(userId)).thenReturn(List.of(highRating));
        when(mediaRepository.findById(ratedMediaId)).thenReturn(Optional.of(ratedMedia));
        when(mediaRepository.findAll()).thenReturn(List.of(ratedMedia));

        List<MediaEntry> result = recommendationService.getGenreBasedRecommendations(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getContentBasedRecommendations_shouldMatchTypeAgeAndGenre() {
        UUID userId = UUID.randomUUID();
        UUID ratedMediaId = UUID.randomUUID();
        UUID matchingMediaId = UUID.randomUUID();
        UUID nonMatchingMediaId = UUID.randomUUID();

        MediaEntry ratedMedia = new MediaEntry(ratedMediaId, "Inception", "Description",
            MediaType.MOVIE, 2010, 12, 5.0, List.of("sci-fi"), userId);
        MediaEntry matchingMedia = new MediaEntry(matchingMediaId, "Interstellar", "Description",
            MediaType.MOVIE, 2014, 12, 4.5, List.of("sci-fi"), userId);
        MediaEntry nonMatchingMedia = new MediaEntry(nonMatchingMediaId, "Breaking Bad", "Description",
            MediaType.SERIES, 2008, 18, 5.0, List.of("drama"), userId);

        Rating highRating = new Rating(ratedMediaId, userId, 5, "Super!");

        when(ratingRepository.findByUserId(userId)).thenReturn(List.of(highRating));
        when(mediaRepository.findById(ratedMediaId)).thenReturn(Optional.of(ratedMedia));
        when(mediaRepository.findAll()).thenReturn(List.of(ratedMedia, matchingMedia, nonMatchingMedia));

        List<MediaEntry> result = recommendationService.getContentBasedRecommendations(userId);

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.getFirst().getTitle());
    }

    @Test
    void getContentBasedRecommendations_withDifferentAgeRestriction_shouldReturnEmpty() {
        UUID userId = UUID.randomUUID();
        UUID ratedMediaId = UUID.randomUUID();
        UUID nonMatchingMediaId = UUID.randomUUID();

        MediaEntry ratedMedia = new MediaEntry(ratedMediaId, "Inception", "Desc",
            MediaType.MOVIE, 2010, 12, 5.0, List.of("sci-fi"), userId);
        MediaEntry nonMatchingMedia = new MediaEntry(nonMatchingMediaId, "Interstellar", "Description",
                MediaType.MOVIE, 2014, 16, 4.5, List.of("sci-fi"), userId);

        Rating highRating = new Rating(ratedMediaId, userId, 5, "Great!");

        when(ratingRepository.findByUserId(userId)).thenReturn(List.of(highRating));
        when(mediaRepository.findById(ratedMediaId)).thenReturn(Optional.of(ratedMedia));
        when(mediaRepository.findAll()).thenReturn(List.of(ratedMedia, nonMatchingMedia));

        List<MediaEntry> result = recommendationService.getContentBasedRecommendations(userId);

        assertTrue(result.isEmpty());
    }
}