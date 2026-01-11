package at.fhtw.swen1.mrp.data.repo;

import at.fhtw.swen1.mrp.business.entities.User;

import java.util.Optional;

public interface UserRepository extends Repository<User> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
