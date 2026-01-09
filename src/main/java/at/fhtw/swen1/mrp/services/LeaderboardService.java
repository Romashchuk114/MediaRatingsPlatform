package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.LeaderboardData;
import at.fhtw.swen1.mrp.business.User;
import at.fhtw.swen1.mrp.data.RatingRepository;
import at.fhtw.swen1.mrp.data.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LeaderboardService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public LeaderboardService(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public List<LeaderboardData> getLeaderboard() {
        List<Object[]> ratingCounts = ratingRepository.getRatingCountsPerUser();
        List<LeaderboardData> leaderboard = new ArrayList<>();

        int rank = 1;
        for (Object[] row : ratingCounts) {
            UUID userId = (UUID) row[0];
            int totalRatings = (int) row[1];

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                LeaderboardData entry = new LeaderboardData(rank, user.getUsername(), totalRatings);
                leaderboard.add(entry);
                rank++;
            }
        }

        return leaderboard;
    }
}