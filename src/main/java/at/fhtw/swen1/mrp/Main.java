package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseConnection;
import at.fhtw.swen1.mrp.data.MediaRepository;
import at.fhtw.swen1.mrp.data.TokenRepository;
import at.fhtw.swen1.mrp.data.UserRepository;
import at.fhtw.swen1.mrp.presentation.controller.MediaController;
import at.fhtw.swen1.mrp.presentation.controller.UserController;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Server;
import at.fhtw.swen1.mrp.presentation.httpserver.utils.Router;
import at.fhtw.swen1.mrp.services.MediaService;
import at.fhtw.swen1.mrp.services.TokenService;
import at.fhtw.swen1.mrp.services.UserService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection dbConnection = new DatabaseConnection(
                "jdbc:postgresql://localhost:5432/mrp_db",
                "mrp_user",
                "mrp_password"
        );

        UserRepository userRepository = new UserRepository(dbConnection);
        MediaRepository mediaRepository = new MediaRepository(dbConnection);
        TokenRepository tokenRepository = new TokenRepository(dbConnection);

        UserService userService = new UserService(userRepository);
        MediaService mediaService = new MediaService(mediaRepository, userRepository);
        TokenService tokenService = new TokenService(tokenRepository);

        UserController userController = new UserController(userService, tokenService);
        MediaController mediaController = new MediaController(mediaService, tokenService);

        Router router = new Router();

        // Controller registrieren
        router.addController("/api/users", userController);
        router.addController("/api/media", mediaController);

        Server server = new Server(8080, router);

        try {
            server.start();

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}