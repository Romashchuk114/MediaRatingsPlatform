package at.fhtw.swen1.mrp.business;


import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private String favoriteGenre;

    public User(String username, String password) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.username = username;
        this.password = password;
    }

    public User(UUID id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(UUID id, String username, String password, String email, String favoriteGenre) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.favoriteGenre = favoriteGenre;
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

    public String getEmail() {
        return email;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }
}
