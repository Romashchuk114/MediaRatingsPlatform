package at.fhtw.swen1.mrp.data.repo;

import at.fhtw.swen1.mrp.business.entities.MediaEntry;

import java.util.List;

public interface MediaRepository extends Repository<MediaEntry> {
    List<MediaEntry> search(String title, String genre, String mediaType,
                            Integer releaseYear, Integer ageRestriction, Double rating,
                            String sortBy, String sortOrder);
}
