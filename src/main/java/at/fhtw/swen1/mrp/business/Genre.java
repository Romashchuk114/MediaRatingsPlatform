package at.fhtw.swen1.mrp.business;

import java.util.List;

public class Genre {

    private Long id;
    private String genreName;

    private List<MediaEntry> mediaEntries;


    public Genre(String genreName, List<MediaEntry> mediaEntries) {
        this.genreName = genreName;
        this.mediaEntries = mediaEntries;
    }

    public Genre() {

    }

    public Long getId() {
        return id;
    }

    public String getGenreName() {
        return genreName;
    }

    public List<MediaEntry> getMediaEntries() {
        return mediaEntries;
    }


    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public void setMediaEntries(List<MediaEntry> mediaEntries) {
        this.mediaEntries = mediaEntries;
    }
}
