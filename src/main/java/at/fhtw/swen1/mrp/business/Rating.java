package at.fhtw.swen1.mrp.business;

import com.github.f4b6a3.uuid.UuidCreator;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rating {

    private UUID id;
    private UUID mediaId;
    private UUID userId;
    private int stars;
    private String comment;
    private boolean isPublic;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Rating(UUID mediaId, UUID userId, int stars, String comment) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.mediaId = mediaId;
        this.userId = userId;
        setStars(stars);
        this.comment = comment;
        this.isPublic = false;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Rating(UUID id, UUID mediaId, UUID userId, int stars, String comment,
                  boolean isPublic, int likeCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.mediaId = mediaId;
        this.userId = userId;
        this.stars = stars;
        this.comment = comment;
        this.isPublic = isPublic;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStars(int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }
        this.stars = stars;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
