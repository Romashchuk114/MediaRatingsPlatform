package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.LeaderboardData;
import at.fhtw.swen1.mrp.presentation.dto.LeaderboardEntryDTO;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.LeaderboardService;
import at.fhtw.swen1.mrp.services.TokenService;

import java.util.List;

public class LeaderboardController extends BaseController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService, TokenService tokenService) {
        super(tokenService);
        this.leaderboardService = leaderboardService;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            // GET /api/leaderboard
            if (matchesRoute(request, Method.GET, 2)) {
                return handleGetLeaderboard();
            }

            return notFound("Endpoint not found");

        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private Response handleGetLeaderboard() {
        List<LeaderboardData> leaderboardData = leaderboardService.getLeaderboard();

        List<LeaderboardEntryDTO> leaderboard = leaderboardData.stream()
                .map(data -> new LeaderboardEntryDTO(data.rank(), data.username(), data.totalRatings()))
                .toList();

        return ok(leaderboard);
    }
}