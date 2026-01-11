package at.fhtw.swen1.mrp.data.jdbc;

import at.fhtw.swen1.mrp.data.DatabaseConnection;
import at.fhtw.swen1.mrp.data.repo.TokenRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class JdbcTokenRepository implements TokenRepository {
    private final DatabaseConnection dbConnection;

    public JdbcTokenRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void save(String token, UUID userId) {
        String sql = "INSERT INTO tokens (token, user_id) VALUES (?, ?) " +
                "ON CONFLICT (token) DO UPDATE SET user_id = EXCLUDED.user_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setObject(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving token: " + e.getMessage(), e);
        }
    }

    public Optional<UUID> findUserIdByToken(String token) {
        String sql = "SELECT user_id FROM tokens WHERE token = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of((UUID) rs.getObject("user_id"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by token: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public boolean exists(String token) {
        String sql = "SELECT 1 FROM tokens WHERE token = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking token existence: " + e.getMessage(), e);
        }
    }

    public void delete(String token) {
        String sql = "DELETE FROM tokens WHERE token = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting token: " + e.getMessage(), e);
        }
    }

}
