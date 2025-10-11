package at.fhtw.swen1.mrp.business;


import com.github.f4b6a3.uuid.UuidCreator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rating {

    private UUID id;

    private int rating;      //stars
    private String comment;
    private LocalDateTime timestamp;
    private boolean isPublic;

    private MediaEntry mediaEntry;

    private User user;

    private List<UUID> likedByUsers = new ArrayList<>();


    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public Rating(int rating, String comment, MediaEntry mediaEntry, User user, List<UUID> likedByUsers) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.rating = rating;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
        this.isPublic = false;
        this.mediaEntry = mediaEntry;
        this.user = user;
        this.likedByUsers = likedByUsers;
    }

    public Rating() {

    }
}
