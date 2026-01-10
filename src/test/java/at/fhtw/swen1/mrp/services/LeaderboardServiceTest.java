package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.LeaderboardData;
import at.fhtw.swen1.mrp.business.User;
import at.fhtw.swen1.mrp.business.UserRatingCount;
import at.fhtw.swen1.mrp.data.RatingRepository;
import at.fhtw.swen1.mrp.data.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    private LeaderboardService leaderboardService;

    @BeforeEach
    void setUp() {
        leaderboardService = new LeaderboardService(ratingRepository, userRepository);
    }

    @Test
    void getLeaderboard_shouldReturnSortedByRatingCount() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UUID user3Id = UUID.randomUUID();

        List<UserRatingCount> ratingCounts = new ArrayList<>();
        ratingCounts.add(new UserRatingCount(user1Id, 10));
        ratingCounts.add(new UserRatingCount(user2Id, 5));
        ratingCounts.add(new UserRatingCount(user3Id, 3));

        when(ratingRepository.getRatingCountsPerUser()).thenReturn(ratingCounts);
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(new User("user1", "hash")));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(new User("user2", "hash")));
        when(userRepository.findById(user3Id)).thenReturn(Optional.of(new User("user3", "hash")));

        List<LeaderboardData> result = leaderboardService.getLeaderboard();

        assertEquals(3, result.size());
        assertEquals(1, result.getFirst().rank());
        assertEquals("user1", result.getFirst().username());
        assertEquals(10, result.getFirst().totalRatings());
        assertEquals(2, result.get(1).rank());
        assertEquals(3, result.get(2).rank());
    }

    @Test
    void getLeaderboard_withNoRatings_shouldReturnEmptyList() {
        when(ratingRepository.getRatingCountsPerUser()).thenReturn(new ArrayList<>());

        List<LeaderboardData> result = leaderboardService.getLeaderboard();

        assertTrue(result.isEmpty());
    }
}