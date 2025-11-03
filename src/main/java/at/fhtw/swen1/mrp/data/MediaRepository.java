package at.fhtw.swen1.mrp.data;

import at.fhtw.swen1.mrp.business.MediaEntry;
import at.fhtw.swen1.mrp.business.MediaType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MediaRepository {
    private final DatabaseConnection dbConnection;

    public MediaRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public MediaEntry save(MediaEntry mediaEntry) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO media_entries (id, title, description, media_type, " +
                    "release_year, age_restriction, average_score, creator_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET " +
                    "title = ?, description = ?, media_type = ?, release_year = ?, " +
                    "age_restriction = ?, average_score = ?, creator_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, mediaEntry.getId());
                stmt.setString(2, mediaEntry.getTitle());
                stmt.setString(3, mediaEntry.getDescription());
                stmt.setString(4, mediaEntry.getMediaType().name());
                stmt.setInt(5, mediaEntry.getReleaseYear());
                stmt.setInt(6, mediaEntry.getAgeRestriction());
                stmt.setDouble(7, mediaEntry.getAverageScore());
                stmt.setObject(8, mediaEntry.getCreatorId());

                // For update part
                stmt.setString(9, mediaEntry.getTitle());
                stmt.setString(10, mediaEntry.getDescription());
                stmt.setString(11, mediaEntry.getMediaType().name());
                stmt.setInt(12, mediaEntry.getReleaseYear());
                stmt.setInt(13, mediaEntry.getAgeRestriction());
                stmt.setDouble(14, mediaEntry.getAverageScore());
                stmt.setObject(15, mediaEntry.getCreatorId());

                stmt.executeUpdate();
            }

            String deleteSql = "DELETE FROM media_genres WHERE media_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setObject(1, mediaEntry.getId());
                stmt.executeUpdate();
            }

            String genreSql = "INSERT INTO media_genres (media_id, genre) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(genreSql)) {
                for (String genre : mediaEntry.getGenres()) {
                    stmt.setObject(1, mediaEntry.getId());
                    stmt.setString(2, genre);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return mediaEntry;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Error saving media entry: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public Optional<MediaEntry> findById(UUID id) {
        String sql = "SELECT id, title, description, media_type, release_year, " +
                "age_restriction, average_score, creator_id FROM media_entries WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MediaEntry mediaEntry = mapResultSetToMediaEntry(rs);
                    loadGenres(mediaEntry);
                    return Optional.of(mediaEntry);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding media entry by id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<MediaEntry> findAll() {
        String sql = "SELECT id, title, description, media_type, release_year, " +
                "age_restriction, average_score, creator_id FROM media_entries";
        List<MediaEntry> mediaEntries = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MediaEntry mediaEntry = mapResultSetToMediaEntry(rs);
                loadGenres(mediaEntry);
                mediaEntries.add(mediaEntry);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all media entries: " + e.getMessage(), e);
        }

        return mediaEntries;
    }


    public MediaEntry delete(UUID id) {
        Optional<MediaEntry> mediaEntry = findById(id);
        if (mediaEntry.isEmpty()) {
            return null;
        }

        String sql = "DELETE FROM media_entries WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();

            return mediaEntry.get();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting media entry: " + e.getMessage(), e);
        }
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM media_entries WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking media entry existence: " + e.getMessage(), e);
        }

        return false;
    }

    private MediaEntry mapResultSetToMediaEntry(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        MediaType mediaType = MediaType.valueOf(rs.getString("media_type"));
        int releaseYear = rs.getInt("release_year");
        int ageRestriction = rs.getInt("age_restriction");
        double averageScore = rs.getDouble("average_score");
        UUID creatorId = (UUID) rs.getObject("creator_id");

        MediaEntry mediaEntry = new MediaEntry(
                title,
                description,
                mediaType,
                releaseYear,
                ageRestriction,
                new ArrayList<>(),
                creatorId
        );

        setMediaEntryId(mediaEntry, id);
        mediaEntry.setAverageScore(averageScore);

        return mediaEntry;
    }

    private void loadGenres(MediaEntry mediaEntry) {
        String sql = "SELECT genre FROM media_genres WHERE media_id = ?";
        List<String> genres = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, mediaEntry.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(rs.getString("genre"));
                }
            }

            mediaEntry.setGenres(genres);

        } catch (SQLException e) {
            throw new RuntimeException("Error loading genres: " + e.getMessage(), e);
        }
    }

    private void setMediaEntryId(MediaEntry mediaEntry, UUID id) {
        try {
            java.lang.reflect.Field idField = MediaEntry.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mediaEntry, id);
        } catch (Exception e) {
            throw new RuntimeException("Error setting media entry id: " + e.getMessage(), e);
        }
    }
}
