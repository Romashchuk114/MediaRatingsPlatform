package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.entities.MediaEntry;
import at.fhtw.swen1.mrp.business.enums.MediaType;
import at.fhtw.swen1.mrp.business.entities.User;
import at.fhtw.swen1.mrp.data.repo.MediaRepository;
import at.fhtw.swen1.mrp.data.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private UserRepository userRepository;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(mediaRepository, userRepository);
    }

    @Test
    void createMedia_withValidData_shouldCreateMedia() {
        UUID creatorId = UUID.randomUUID();
        User creator = new User("testuser", "hashedPassword");

        when(userRepository.findById(creatorId)).thenReturn(Optional.of(creator));
        when(mediaRepository.save(any(MediaEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MediaEntry result = mediaService.createMedia(
            "Inception", "Description", "movie",
            2010, List.of("sci-fi", "thriller"), 12, creatorId
        );

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        assertEquals(MediaType.MOVIE, result.getMediaType());
    }

    @Test
    void createMedia_withEmptyTitle_shouldThrowException() {
        UUID creatorId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
            mediaService.createMedia("", "Desc", "movie", 2010, List.of("sci-fi"), 12, creatorId)
        );
    }

    @Test
    void createMedia_withInvalidMediaType_shouldThrowException() {
        UUID creatorId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
            mediaService.createMedia("Title", "Desc", "invalid_type", 2010, List.of("sci-fi"), 12, creatorId)
        );
    }

    @Test
    void createMedia_withNonExistentCreator_shouldThrowException() {
        UUID creatorId = UUID.randomUUID();

        when(userRepository.findById(creatorId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            mediaService.createMedia("Title", "Desc", "movie", 2010, List.of("sci-fi"), 12, creatorId)
        );
    }

    @Test
    void updateMedia_byCreator_shouldUpdateMedia() {
        UUID mediaId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        MediaEntry media = new MediaEntry("Old Title", "Old Desc", MediaType.MOVIE, 2010, 12, List.of("action"), creatorId);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        when(mediaRepository.save(any(MediaEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MediaEntry result = mediaService.updateMedia(
            mediaId, "New Title", "New Desc", "series", 2020, List.of("drama"), 16, creatorId
        );

        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        assertEquals(MediaType.SERIES, result.getMediaType());
        assertEquals(2020, result.getReleaseYear());
        assertEquals("drama", result.getGenres().getFirst());
        assertEquals(16, result.getAgeRestriction());

    }

    @Test
    void updateMedia_byNonCreator_shouldThrowException() {
        UUID mediaId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        MediaEntry media = new MediaEntry("Title", "Desc", MediaType.MOVIE, 2010, 12, List.of("action"), creatorId);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        assertThrows(SecurityException.class, () ->
            mediaService.updateMedia(mediaId, "New Title", "New Desc", "movie", 2020, List.of("drama"), 16, otherUserId)
        );
    }

    @Test
    void deleteMedia_byCreator_shouldDeleteMedia() {
        UUID mediaId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        MediaEntry media = new MediaEntry("Title", "Desc", MediaType.MOVIE, 2010, 12, List.of("action"), creatorId);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        mediaService.deleteMedia(mediaId, creatorId);

        verify(mediaRepository).delete(mediaId);
    }

    @Test
    void deleteMedia_byNonCreator_shouldThrowException() {
        UUID mediaId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        MediaEntry media = new MediaEntry("Title", "Desc", MediaType.MOVIE, 2010, 12, List.of("action"), creatorId);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        assertThrows(SecurityException.class, () ->
            mediaService.deleteMedia(mediaId, otherUserId)
        );
    }
}