package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.data.MediaRepository;

public class MediaService {
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }


}
