package at.fhtw.swen1.mrp.data;

import at.fhtw.swen1.mrp.business.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RatingRepository implements Repository<Rating> {
    private final DatabaseConnection dbConnection;

    public RatingRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Rating save(Rating rating) {
        String sql = "INSERT INTO ratings (id, media_id, user_id, stars, comment, is_public, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "stars = EXCLUDED.stars, comment = EXCLUDED.comment, " +
                "is_public = EXCLUDED.is_public, updated_at = EXCLUDED.updated_at";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, rating.getId());
            stmt.setObject(2, rating.getMediaId());
            stmt.setObject(3, rating.getUserId());
            stmt.setInt(4, rating.getStars());
            stmt.setString(5, rating.getComment());
            stmt.setBoolean(6, rating.isPublic());
            stmt.setTimestamp(7, Timestamp.valueOf(rating.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(rating.getUpdatedAt()));

            stmt.executeUpdate();
            return rating;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving rating: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Rating> findById(UUID id) {
        String sql = "SELECT id, media_id, user_id, stars, comment, is_public, created_at, updated_at " +
                "FROM ratings WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRating(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding rating by id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<Rating> findAll() {
        String sql = "SELECT id, media_id, user_id, stars, comment, is_public, created_at, updated_at FROM ratings";
        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all ratings: " + e.getMessage(), e);
        }

        return ratings;
    }

    @Override
    public Optional<Rating> delete(UUID id) {
        Optional<Rating> rating = findById(id);
        if (rating.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM ratings WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting rating: " + e.getMessage(), e);
        }

        return rating;
    }

    @Override
    public boolean existsById(UUID id) {
        String sql = "SELECT 1 FROM ratings WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking rating existence: " + e.getMessage(), e);
        }
    }

    public List<Rating> findByMediaId(UUID mediaId) {
        String sql = "SELECT id, media_id, user_id, stars, comment, is_public, created_at, updated_at " +
                "FROM ratings WHERE media_id = ?";
        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, mediaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding ratings by media id: " + e.getMessage(), e);
        }

        return ratings;
    }

    public List<Rating> findByUserId(UUID userId) {
        String sql = "SELECT id, media_id, user_id, stars, comment, is_public, created_at, updated_at " +
                "FROM ratings WHERE user_id = ?";
        List<Rating> ratings = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding ratings by user id: " + e.getMessage(), e);
        }

        return ratings;
    }

    public Optional<Rating> findByMediaIdAndUserId(UUID mediaId, UUID userId) {
        String sql = "SELECT id, media_id, user_id, stars, comment, is_public, created_at, updated_at " +
                "FROM ratings WHERE media_id = ? AND user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, mediaId);
            stmt.setObject(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRating(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding rating by media and user id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public void addLike(UUID ratingId, UUID userId) {
        String sql = "INSERT INTO rating_likes (rating_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, ratingId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding like: " + e.getMessage(), e);
        }
    }

    public void removeLike(UUID ratingId, UUID userId) {
        String sql = "DELETE FROM rating_likes WHERE rating_id = ? AND user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, ratingId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error removing like: " + e.getMessage(), e);
        }
    }

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        return new Rating(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("media_id"),
                (UUID) rs.getObject("user_id"),
                rs.getInt("stars"),
                rs.getString("comment"),
                rs.getBoolean("is_public"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
