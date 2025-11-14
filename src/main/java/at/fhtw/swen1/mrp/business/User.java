package at.fhtw.swen1.mrp.business;


import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String password;    //TODO passwort hashen

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

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
