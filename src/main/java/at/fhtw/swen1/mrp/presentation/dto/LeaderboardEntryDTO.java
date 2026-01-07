package at.fhtw.swen1.mrp.presentation.dto;

public class LeaderboardEntryDTO {
    private int rank;
    private String username;
    private int totalRatings;

    public LeaderboardEntryDTO() {
    }

    public LeaderboardEntryDTO(int rank, String username, int totalRatings) {
        this.rank = rank;
        this.username = username;
        this.totalRatings = totalRatings;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }
}