package at.fhtw.swen1.mrp.data.jdbc;

import at.fhtw.swen1.mrp.business.entities.MediaEntry;
import at.fhtw.swen1.mrp.business.enums.MediaType;
import at.fhtw.swen1.mrp.data.DatabaseConnection;
import at.fhtw.swen1.mrp.data.repo.MediaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class JdbcMediaRepository implements MediaRepository {
    private final DatabaseConnection dbConnection;

    public JdbcMediaRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public MediaEntry save(MediaEntry mediaEntry) {
        String sql = "INSERT INTO media_entries (id, title, description, media_type, " +
                "release_year, age_restriction, average_score, creator_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "title = EXCLUDED.title, description = EXCLUDED.description, " +
                "media_type = EXCLUDED.media_type, release_year = EXCLUDED.release_year, " +
                "age_restriction = EXCLUDED.age_restriction, average_score = EXCLUDED.average_score";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, mediaEntry.getId());
                stmt.setString(2, mediaEntry.getTitle());
                stmt.setString(3, mediaEntry.getDescription());
                stmt.setString(4, mediaEntry.getMediaType().name());
                stmt.setInt(5, mediaEntry.getReleaseYear());
                stmt.setInt(6, mediaEntry.getAgeRestriction());
                stmt.setDouble(7, mediaEntry.getAverageScore());
                stmt.setObject(8, mediaEntry.getCreatorId());
                stmt.executeUpdate();
            }

            saveGenres(conn, mediaEntry);
            conn.commit();
            return mediaEntry;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving media entry: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<MediaEntry> findById(UUID id) {
        String sql = "SELECT id, title, description, media_type, release_year, " +
                "age_restriction, average_score, creator_id FROM media_entries WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MediaEntry mediaEntry = mapResultSetToMediaEntry(rs);
                    mediaEntry.setGenres(loadGenres(id));
                    return Optional.of(mediaEntry);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding media entry by id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<MediaEntry> findAll() {
        String sql = "SELECT id, title, description, media_type, release_year, " +
                "age_restriction, average_score, creator_id FROM media_entries";
        List<MediaEntry> mediaEntries = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MediaEntry mediaEntry = mapResultSetToMediaEntry(rs);
                mediaEntry.setGenres(loadGenres(mediaEntry.getId()));
                mediaEntries.add(mediaEntry);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all media entries: " + e.getMessage(), e);
        }

        return mediaEntries;
    }

    @Override
    public Optional<MediaEntry> delete(UUID id) {
        Optional<MediaEntry> mediaEntry = findById(id);
        if (mediaEntry.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM media_entries WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting media entry: " + e.getMessage(), e);
        }

        return mediaEntry;
    }

    @Override
    public boolean existsById(UUID id) {
        String sql = "SELECT 1 FROM media_entries WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking media entry existence: " + e.getMessage(), e);
        }
    }

    public List<MediaEntry> search(String title, String genre, String mediaType,
                                    Integer releaseYear, Integer ageRestriction, Double rating,
                                    String sortBy, String sortOrder) {

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT m.id, m.title, m.description, m.media_type, m.release_year, " +
                        "m.age_restriction, m.average_score, m.creator_id FROM media_entries m ");

        boolean hasGenreFilter = genre != null && !genre.isEmpty();
        boolean hasTitleFilter = title != null && !title.isEmpty();
        boolean hasMediaTypeFilter = mediaType != null && !mediaType.isEmpty();

        if (hasGenreFilter) {
            sql.append("JOIN media_genres g ON m.id = g.media_id ");
        }

        sql.append("WHERE 1=1 ");

        if (hasTitleFilter) {
            sql.append("AND LOWER(m.title) LIKE LOWER(?) ");
        }
        if (hasGenreFilter) {
            sql.append("AND LOWER(g.genre) = LOWER(?) ");
        }
        if (hasMediaTypeFilter) {
            sql.append("AND m.media_type = ? ");
        }
        if (releaseYear != null) {
            sql.append("AND m.release_year = ? ");
        }
        if (ageRestriction != null) {
            sql.append("AND m.age_restriction <= ? ");
        }
        if (rating != null) {
            sql.append("AND m.average_score >= ? ");
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("year")) {
            sql.append("ORDER BY m.release_year ");
        } else if (sortBy != null && sortBy.equalsIgnoreCase("score")) {
            sql.append("ORDER BY m.average_score ");
        } else if (sortBy != null && sortBy.equalsIgnoreCase("title")) {
            sql.append("ORDER BY m.title ");
        } else {
            sql.append("ORDER BY m.title ");
        }

        if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
            sql.append("DESC");
        } else {
            sql.append("ASC");
        }

        List<MediaEntry> results = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasTitleFilter) {
                stmt.setString(paramIndex++, "%" + title + "%");
            }
            if (hasGenreFilter) {
                stmt.setString(paramIndex++, genre);
            }
            if (hasMediaTypeFilter) {
                stmt.setString(paramIndex++, mediaType.toUpperCase());
            }
            if (releaseYear != null) {
                stmt.setInt(paramIndex++, releaseYear);
            }
            if (ageRestriction != null) {
                stmt.setInt(paramIndex++, ageRestriction);
            }
            if (rating != null) {
                stmt.setDouble(paramIndex++, rating);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MediaEntry entry = mapResultSetToMediaEntry(rs);
                    entry.setGenres(loadGenres(entry.getId()));
                    results.add(entry);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error searching media: " + e.getMessage(), e);
        }

        return results;
    }

    private void saveGenres(Connection conn, MediaEntry mediaEntry) throws SQLException {
        String deleteSql = "DELETE FROM media_genres WHERE media_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setObject(1, mediaEntry.getId());
            stmt.executeUpdate();
        }

        String insertSql = "INSERT INTO media_genres (media_id, genre) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (String genre : mediaEntry.getGenres()) {
                stmt.setObject(1, mediaEntry.getId());
                stmt.setString(2, genre);
                stmt.executeUpdate();
            }
        }
    }

    private List<String> loadGenres(UUID mediaId) {
        String sql = "SELECT genre FROM media_genres WHERE media_id = ?";
        List<String> genres = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, mediaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(rs.getString("genre"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading genres: " + e.getMessage(), e);
        }

        return genres;
    }

    private MediaEntry mapResultSetToMediaEntry(ResultSet rs) throws SQLException {
        return new MediaEntry(
                (UUID) rs.getObject("id"),
                rs.getString("title"),
                rs.getString("description"),
                MediaType.valueOf(rs.getString("media_type")),
                rs.getInt("release_year"),
                rs.getInt("age_restriction"),
                rs.getDouble("average_score"),
                new ArrayList<>(),
                (UUID) rs.getObject("creator_id")
        );
    }
}
