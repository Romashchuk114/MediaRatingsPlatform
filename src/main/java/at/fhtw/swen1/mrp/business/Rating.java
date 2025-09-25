package at.fhtw.swen1.mrp.business;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Rating {

    private Long id;

    private int rating;      //stars
    private String comment;
    private LocalDateTime timestamp;
    private boolean isPublic = false;

    private MediaEntry mediaEntry;

    private User user;

    private List<User> likedByUsers = new ArrayList<>();


    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public Rating(int rating, String comment, boolean isPublic, MediaEntry mediaEntry, User user, List<User> likedByUsers) {
        this.rating = rating;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
        this.isPublic = isPublic;
        this.mediaEntry = mediaEntry;
        this.user = user;
        this.likedByUsers = likedByUsers;
    }

    public Rating() {

    }
}
