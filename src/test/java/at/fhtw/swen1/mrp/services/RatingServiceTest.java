package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.entities.MediaEntry;
import at.fhtw.swen1.mrp.business.enums.MediaType;
import at.fhtw.swen1.mrp.business.entities.Rating;
import at.fhtw.swen1.mrp.data.repo.MediaRepository;
import at.fhtw.swen1.mrp.data.repo.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MediaRepository mediaRepository;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(ratingRepository, mediaRepository);
    }

    @Test
    void createRating_withValidData_shouldCreateRating() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(mediaRepository.existsById(mediaId)).thenReturn(true);
        when(ratingRepository.findByMediaIdAndUserId(mediaId, userId)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ratingRepository.findByMediaId(mediaId)).thenReturn(new ArrayList<>());
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(
            new MediaEntry("Test", "Description", MediaType.MOVIE, 2020, 12, new ArrayList<>(), userId)
        ));

        Rating result = ratingService.createRating(mediaId, userId, 5, "Super!");

        assertNotNull(result);
        assertEquals(5, result.getStars());
        assertEquals("Super!", result.getComment());
    }

    @Test
    void createRating_withNonExistentMedia_shouldThrowException() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(mediaRepository.existsById(mediaId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            ratingService.createRating(mediaId, userId, 5, "Super!")
        );
    }

    @Test
    void createRating_whenUserAlreadyRated_shouldThrowException() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Rating existingRating = new Rating(mediaId, userId, 4, "old rating");

        when(mediaRepository.existsById(mediaId)).thenReturn(true);
        when(ratingRepository.findByMediaIdAndUserId(mediaId, userId)).thenReturn(Optional.of(existingRating));

        assertThrows(IllegalArgumentException.class, () ->
            ratingService.createRating(mediaId, userId, 5, "New rating")
        );
    }

    @Test
    void updateRating_byOwner_shouldUpdateRating() {
        UUID ratingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        Rating rating = new Rating(mediaId, userId, 3, "Old comment");

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ratingRepository.findByMediaId(mediaId)).thenReturn(List.of(rating));
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(
            new MediaEntry("Test", "Desc", MediaType.MOVIE, 2020, 12, new ArrayList<>(), userId)
        ));

        Rating result = ratingService.updateRating(ratingId, userId, 5, "Updated comment");

        assertEquals(5, result.getStars());
        assertEquals("Updated comment", result.getComment());
    }

    @Test
    void updateRating_byNonOwner_shouldThrowException() {
        UUID ratingId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Rating rating = new Rating(ratingId, ownerId, 3, "Comment");

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        assertThrows(IllegalArgumentException.class, () ->
            ratingService.updateRating(ratingId, otherUserId, 5, "comment update")
        );
    }

    @Test
    void likeRating_shouldAddLike() {
        UUID ratingId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID likerId = UUID.randomUUID();
        Rating rating = new Rating(ratingId, ownerId, 5, "super!");

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(ratingRepository.hasUserLiked(ratingId, likerId)).thenReturn(false);

        ratingService.likeRating(ratingId, likerId);

        verify(ratingRepository).addLike(ratingId, likerId);
    }

    @Test
    void likeRating_ownRating_shouldThrowException() {
        UUID ratingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Rating rating = new Rating(UUID.randomUUID(), userId, 5, "My rating");

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        assertThrows(IllegalArgumentException.class, () ->
            ratingService.likeRating(ratingId, userId)
        );
    }

    @Test
    void getTotalRatingsForUser_shouldReturnCount() {
        UUID userId = UUID.randomUUID();
        List<Rating> ratings = List.of(
            new Rating(UUID.randomUUID(), userId, 5, "R1"),
            new Rating(UUID.randomUUID(), userId, 4, "R2"),
            new Rating(UUID.randomUUID(), userId, 3, "R3")
        );

        when(ratingRepository.findByUserId(userId)).thenReturn(ratings);

        int result = ratingService.getTotalRatingsForUser(userId);

        assertEquals(3, result);
    }

    @Test
    void getAverageScoreForUser_shouldCalculateAverage() {
        UUID userId = UUID.randomUUID();
        List<Rating> ratings = List.of(
            new Rating(UUID.randomUUID(), userId, 5, "R1"),
            new Rating(UUID.randomUUID(), userId, 4, "R2"),
            new Rating(UUID.randomUUID(), userId, 3, "R3")
        );

        when(ratingRepository.findByUserId(userId)).thenReturn(ratings);

        double result = ratingService.getAverageScoreForUser(userId);

        assertEquals(4.0, result);
    }
}