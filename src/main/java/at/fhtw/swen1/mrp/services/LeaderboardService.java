package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.transfer.LeaderboardData;
import at.fhtw.swen1.mrp.business.entities.User;
import at.fhtw.swen1.mrp.business.transfer.UserRatingCount;
import at.fhtw.swen1.mrp.data.repo.RatingRepository;
import at.fhtw.swen1.mrp.data.repo.UserRepository;

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
        List<UserRatingCount> ratingCounts = ratingRepository.getRatingCountsPerUser();
        List<LeaderboardData> leaderboard = new ArrayList<>();

        int rank = 1;
        for (UserRatingCount item : ratingCounts) {
            UUID userId = item.userId();
            int totalRatings = item.count();

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