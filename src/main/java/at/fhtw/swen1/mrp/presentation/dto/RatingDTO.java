package at.fhtw.swen1.mrp.presentation.dto;

import at.fhtw.swen1.mrp.business.Rating;

import java.time.LocalDateTime;
import java.util.UUID;

public class RatingDTO {
    private UUID id;
    private UUID mediaId;
    private UUID userId;
    private int stars;
    private String comment;
    private boolean isPublic;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RatingDTO() {
    }

    public RatingDTO(Rating rating) {
        this.id = rating.getId();
        this.mediaId = rating.getMediaId();
        this.userId = rating.getUserId();
        this.stars = rating.getStars();
        this.comment = rating.getComment();
        this.isPublic = rating.isPublic();
        this.likeCount = rating.getLikeCount();
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
