package at.fhtw.swen1.mrp.data;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.MediaType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MediaRepository {
    private final Map<UUID, MediaEntry> mediaEntries;

    public MediaRepository() {
        this.mediaEntries = new ConcurrentHashMap<>();
    }

    public MediaEntry save(MediaEntry mediaEntry) {
        mediaEntries.put(mediaEntry.getId(), mediaEntry);
        return mediaEntry;
    }

    public Optional<MediaEntry> findById(UUID id) {
        return Optional.ofNullable(mediaEntries.get(id));
    }

    public List<MediaEntry> findAll() {
        return new ArrayList<>(mediaEntries.values());
    }

    public List<MediaEntry> findByTitle(String title) {
        return mediaEntries.values().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<MediaEntry> findByGenre(String genre) {
        return mediaEntries.values().stream()
                .filter(m -> m.getGenres().stream()
                        .anyMatch(g -> g.equalsIgnoreCase(genre)))
                .collect(Collectors.toList());
    }

    public List<MediaEntry> findByMediaType(MediaType mediaType) {
        return mediaEntries.values().stream()
                .filter(m -> m.getMediaType() == mediaType)
                .collect(Collectors.toList());
    }

    public List<MediaEntry> findByCreatorId(UUID creatorId) {
        return mediaEntries.values().stream()
                .filter(m -> m.getCreatorId().equals(creatorId))
                .collect(Collectors.toList());
    }

    public MediaEntry update(MediaEntry media) {
        if (mediaEntries.containsKey(media.getId())) {
            mediaEntries.put(media.getId(), media);
            return media;
        }
        return null;
    }

    public MediaEntry delete(UUID id) {
        return mediaEntries.remove(id);
    }

    public boolean existsById(UUID id) {
        return mediaEntries.containsKey(id);
    }
}
