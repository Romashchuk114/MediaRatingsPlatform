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
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        DatabaseConnection dbConnection = new DatabaseConnection(dbUrl, dbUser, dbPassword);

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