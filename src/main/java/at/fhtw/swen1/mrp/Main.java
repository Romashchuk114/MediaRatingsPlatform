package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseConnection;
import at.fhtw.swen1.mrp.data.FavoriteRepository;
import at.fhtw.swen1.mrp.data.MediaRepository;
import at.fhtw.swen1.mrp.data.RatingRepository;
import at.fhtw.swen1.mrp.data.TokenRepository;
import at.fhtw.swen1.mrp.data.UserRepository;
import at.fhtw.swen1.mrp.presentation.controller.LeaderboardController;
import at.fhtw.swen1.mrp.presentation.controller.MediaController;
import at.fhtw.swen1.mrp.presentation.controller.RatingController;
import at.fhtw.swen1.mrp.presentation.controller.UserController;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Server;
import at.fhtw.swen1.mrp.presentation.httpserver.utils.Router;
import at.fhtw.swen1.mrp.services.FavoriteService;
import at.fhtw.swen1.mrp.services.LeaderboardService;
import at.fhtw.swen1.mrp.services.MediaService;
import at.fhtw.swen1.mrp.services.RecommendationService;
import at.fhtw.swen1.mrp.services.PasswordHasher;
import at.fhtw.swen1.mrp.services.RatingService;
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
        RatingRepository ratingRepository = new RatingRepository(dbConnection);
        FavoriteRepository favoriteRepository = new FavoriteRepository(dbConnection);

        PasswordHasher passwordHasher = new PasswordHasher();
        UserService userService = new UserService(userRepository, passwordHasher);
        MediaService mediaService = new MediaService(mediaRepository, userRepository);
        TokenService tokenService = new TokenService(tokenRepository);
        RatingService ratingService = new RatingService(ratingRepository, mediaRepository);
        FavoriteService favoriteService = new FavoriteService(favoriteRepository, mediaRepository);
        LeaderboardService leaderboardService = new LeaderboardService(ratingRepository, userRepository);
        RecommendationService recommendationService = new RecommendationService(ratingRepository, mediaRepository);

        UserController userController = new UserController(userService, tokenService, ratingService, favoriteService, recommendationService);
        MediaController mediaController = new MediaController(mediaService, ratingService, favoriteService, tokenService);
        RatingController ratingController = new RatingController(ratingService, tokenService);
        LeaderboardController leaderboardController = new LeaderboardController(leaderboardService, tokenService);

        Router router = new Router();

        // Controller registrieren
        router.addController("/api/users", userController);
        router.addController("/api/media", mediaController);
        router.addController("/api/ratings", ratingController);
        router.addController("/api/leaderboard", leaderboardController);

        Server server = new Server(8080, router);

        try {
            server.start();

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}