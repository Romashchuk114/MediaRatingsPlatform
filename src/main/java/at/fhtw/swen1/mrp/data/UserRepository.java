package at.fhtw.swen1.mrp.data;

import at.fhtw.swen1.mrp.business.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserRepository implements Repository<User> {
    private final DatabaseConnection dbConnection;

    public UserRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET username = EXCLUDED.username, password = EXCLUDED.password";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());

            stmt.executeUpdate();
            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = "SELECT id, username, password FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking username existence: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> delete(UUID id) {
        Optional<User> user = findById(id);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username, password FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users: " + e.getMessage(), e);
        }

        return users;
    }

    @Override
    public boolean existsById(UUID id) {
        String sql = "SELECT 1 FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                (UUID) rs.getObject("id"),
                rs.getString("username"),
                rs.getString("password")
        );
    }
}
