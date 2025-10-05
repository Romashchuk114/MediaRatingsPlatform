package at.fhtw.swen1.mrp.business;


import com.github.f4b6a3.uuid.UuidCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MediaEntry {

    private UUID id;
    private String title;
    private String description;

    private MediaType mediaType;
    private int releaseYear;
    private int ageRestriction;
    private double averageScore;

    private List<String> genres = new ArrayList<>();
    private User creator;
    private List<Rating> ratings = new ArrayList<>();
    private List<User> favoritesByUsers = new ArrayList<>();


    public MediaEntry(String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, double averageScore, List<String> genres, User creator, List<Rating> ratings, List<User> favoritesByUsers) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.averageScore = averageScore;
        this.genres = genres;
        this.creator = creator;
        this.ratings = ratings;
        this.favoritesByUsers = favoritesByUsers;
    }

    public MediaEntry() {

    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public User getCreator() {
        return creator;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public List<User> getFavoritesByUsers() {
        return favoritesByUsers;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public void setFavoritesByUsers(List<User> favoritesByUsers) {
        this.favoritesByUsers = favoritesByUsers;
    }
}
