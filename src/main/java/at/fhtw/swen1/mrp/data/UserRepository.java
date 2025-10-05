package at.fhtw.swen1.mrp.data;

import at.fhtw.swen1.mrp.business.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private final Map<UUID, User> usersById;
    private final Map<String, User> usersByUsername;

    public UserRepository() {
        this.usersById = new ConcurrentHashMap<>();
        this.usersByUsername = new ConcurrentHashMap<>();
    }

    public User save(User user) {
        usersById.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }

    public User update(User user) {
        User existing = usersById.get(user.getId());
        if (existing != null) {

            usersById.put(user.getId(), user);
            usersByUsername.put(user.getUsername(), user);
        }
        return user;
    }

    public void delete(UUID id) {
        User user = usersById.remove(id);
        if (user != null) {
            usersByUsername.remove(user.getUsername());
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }
}
