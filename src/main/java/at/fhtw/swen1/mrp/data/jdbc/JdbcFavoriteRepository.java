package at.fhtw.swen1.mrp.data.jdbc;

import at.fhtw.swen1.mrp.business.entities.MediaEntry;
import at.fhtw.swen1.mrp.business.enums.MediaType;
import at.fhtw.swen1.mrp.data.DatabaseConnection;
import at.fhtw.swen1.mrp.data.repo.FavoriteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcFavoriteRepository implements FavoriteRepository {
    private final DatabaseConnection dbConnection;

    public JdbcFavoriteRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addFavorite(UUID userId, UUID mediaId) {
        String sql = "INSERT INTO favorites (user_id, media_id) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setObject(2, mediaId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding favorite: " + e.getMessage(), e);
        }
    }

    public void removeFavorite(UUID userId, UUID mediaId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND media_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setObject(2, mediaId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error removing favorite: " + e.getMessage(), e);
        }
    }

    public boolean isFavorite(UUID userId, UUID mediaId) {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND media_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setObject(2, mediaId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking favorite: " + e.getMessage(), e);
        }
    }

    public List<MediaEntry> findFavoritesByUserId(UUID userId) {
        String sql = "SELECT m.id, m.title, m.description, m.media_type, m.release_year, " +
                "m.age_restriction, m.average_score, m.creator_id " +
                "FROM favorites f " +
                "JOIN media_entries m ON f.media_id = m.id " +
                "WHERE f.user_id = ? " +
                "ORDER BY f.created_at DESC";

        List<MediaEntry> favorites = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MediaEntry entry = mapResultSetToMediaEntry(rs);
                    entry.setGenres(loadGenres(entry.getId()));
                    favorites.add(entry);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding favorites: " + e.getMessage(), e);
        }

        return favorites;
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
