package at.fhtw.swen1.mrp.business;


import java.util.ArrayList;
import java.util.List;

public class User {

    private Long id;
    private String username;
    private String password;    //wird noch geändert

    private List<Rating> ratings = new ArrayList<>();

    private List<MediaEntry> createdMediaEntries = new ArrayList<>();

    private List<MediaEntry> favoriteMediaEntries = new ArrayList<>();

    private List<Rating> likedRatings = new ArrayList<>();


    public User(List<Rating> likedRatings, List<MediaEntry> favoriteMediaEntries, List<MediaEntry> createdMediaEntries, List<Rating> ratings, String password, String username) {
        this.likedRatings = likedRatings;
        this.favoriteMediaEntries = favoriteMediaEntries;
        this.createdMediaEntries = createdMediaEntries;
        this.ratings = ratings;
        this.password = password;
        this.username = username;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public List<MediaEntry> getCreatedMediaEntries() {
        return createdMediaEntries;
    }

    public List<MediaEntry> getFavoriteMediaEntries() {
        return favoriteMediaEntries;
    }

    public List<Rating> getLikedRatings() {
        return likedRatings;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public void setCreatedMediaEntries(List<MediaEntry> createdMediaEntries) {
        this.createdMediaEntries = createdMediaEntries;
    }

    public void setFavoriteMediaEntries(List<MediaEntry> favoriteMediaEntries) {
        this.favoriteMediaEntries = favoriteMediaEntries;
    }

    public void setLikedRatings(List<Rating> likedRatings) {
        this.likedRatings = likedRatings;
    }
}
