package at.fhtw.swen1.mrp.presentation.dto;

import at.fhtw.swen1.mrp.business.MediaEntry;

import java.util.List;
import java.util.UUID;

public class MediaEntryDTO {
    private UUID id;
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private List<String> genres;
    private int ageRestriction;
    private Double averageScore;
    private UUID creatorId;

    public MediaEntryDTO() {
    }

    public MediaEntryDTO(MediaEntry mediaEntry) {
        this.id = mediaEntry.getId();
        this.title = mediaEntry.getTitle();
        this.description = mediaEntry.getDescription();
        this.mediaType = mediaEntry.getMediaType().name().toLowerCase();
        this.releaseYear = mediaEntry.getReleaseYear();
        this.genres = mediaEntry.getGenres();
        this.ageRestriction = mediaEntry.getAgeRestriction();
        this.averageScore = mediaEntry.getAverageScore();
        this.creatorId = mediaEntry.getCreatorId();
    }

    public boolean isValid() {
        return title != null && !title.trim().isEmpty()
                && mediaType != null && !mediaType.trim().isEmpty()
                && genres != null && !genres.isEmpty()
                && releaseYear > 0
                && ageRestriction >= 0;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
