package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.data.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
