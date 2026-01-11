package at.fhtw.swen1.mrp.business.entities;


import at.fhtw.swen1.mrp.business.enums.MediaType;
import com.github.f4b6a3.uuid.UuidCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MediaEntry {

    private UUID id;
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private int ageRestriction;
    private double averageScore;

    private List<String> genres = new ArrayList<>();
    private UUID creatorId;

    public MediaEntry(UUID id, String title, String description, MediaType mediaType, int releaseYear,
                      int ageRestriction, double averageScore, List<String> genres, UUID creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.averageScore = averageScore;
        this.genres = genres;
        this.creatorId = creatorId;
    }

    public MediaEntry(String title, String description, MediaType mediaType, int releaseYear,
                      int ageRestriction, List<String> genres, UUID creatorId) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.creatorId = creatorId;
        this.averageScore = 0.0;
    }

    public MediaEntry() {

    }

    public UUID getId() {
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

    public UUID getCreatorId() {
        return creatorId;
    }

    public List<String> getGenres() {
        return genres;
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

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }
}
