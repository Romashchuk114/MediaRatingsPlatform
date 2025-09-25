package at.fhtw.swen1.mrp.business;


import java.util.ArrayList;
import java.util.List;

public class MediaEntry {

    private Long id;
    private String title;
    private String description;

    private MediaType mediaType;
    private int releaseYear;
    private int ageRestriction;
    private double averageScore;

    private List<Genre> genres = new ArrayList<>();

    private User creator;

    private List<Rating> ratings = new ArrayList<>();

    private List<User> favoritesByUsers = new ArrayList<>();


    public MediaEntry(String title, String description, MediaType mediaType, int releaseYear, int ageRestriction, double averageScore, List<Genre> genres, User creator, List<Rating> ratings, List<User> favoritesByUsers) {
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

    public Long getId() {
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

    public List<Genre> getGenres() {
        return genres;
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

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
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
