package at.fhtw.swen1.mrp.data.repo;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository {
    void save(String token, UUID userId);

    Optional<UUID> findUserIdByToken(String token);

    boolean exists(String token);

    void delete(String token);
}
