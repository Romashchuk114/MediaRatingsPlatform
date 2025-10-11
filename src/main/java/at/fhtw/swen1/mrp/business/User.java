package at.fhtw.swen1.mrp.business;


import com.github.f4b6a3.uuid.UuidCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String password;    //wird noch geändert

    private List<UUID> ratings = new ArrayList<>();

    private List<UUID> createdMediaEntries = new ArrayList<>();

    private List<UUID> favoriteMediaEntries = new ArrayList<>();

    private List<UUID> likedRatings = new ArrayList<>();


    public User(List<UUID> likedRatings, List<UUID> favoriteMediaEntries, List<UUID> createdMediaEntries, List<UUID> ratings, String password, String username) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.likedRatings = likedRatings;
        this.favoriteMediaEntries = favoriteMediaEntries;
        this.createdMediaEntries = createdMediaEntries;
        this.ratings = ratings;
        this.password = password;
        this.username = username;
    }

    public User(String username, String password) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.username = username;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<UUID> getRatings() {
        return ratings;
    }

    public List<UUID> getCreatedMediaEntries() {
        return createdMediaEntries;
    }

    public List<UUID> getFavoriteMediaEntries() {
        return favoriteMediaEntries;
    }

    public List<UUID> getLikedRatings() {
        return likedRatings;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRatings(List<UUID> ratings) {
        this.ratings = ratings;
    }

    public void setCreatedMediaEntries(List<UUID> createdMediaEntries) {
        this.createdMediaEntries = createdMediaEntries;
    }

    public void setFavoriteMediaEntries(List<UUID> favoriteMediaEntries) {
        this.favoriteMediaEntries = favoriteMediaEntries;
    }

    public void setLikedRatings(List<UUID> likedRatings) {
        this.likedRatings = likedRatings;
    }
}
