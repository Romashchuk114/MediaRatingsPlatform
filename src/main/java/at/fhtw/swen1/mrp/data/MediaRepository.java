package at.fhtw.swen1.mrp.data;

import at.fhtw.swen1.mrp.business.MediaEntry;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MediaRepository {
    private final Map<UUID, MediaEntry> mediaEntries;

    public MediaRepository() {
        this.mediaEntries = new ConcurrentHashMap<>();
    }

    public MediaEntry save(MediaEntry mediaEntry) {
        mediaEntries.put(mediaEntry.getId(), mediaEntry);
        return mediaEntry;
    }
}
