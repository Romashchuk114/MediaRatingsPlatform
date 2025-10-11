package at.fhtw.swen1.mrp.data;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenRepository {
    private final Map<String, UUID> tokens;

    public TokenRepository() {
        this.tokens = new ConcurrentHashMap<>();
    }


    public void save(String token, UUID userId) {
        tokens.put(token, userId);
    }

    public Optional<UUID> findUserIdByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }

    public boolean exists(String token) {
        return tokens.containsKey(token);
    }

    public void delete(String token) {
        tokens.remove(token);
    }


}
