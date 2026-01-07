package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.presentation.dto.LeaderboardEntryDTO;
import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.LeaderboardService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class LeaderboardController implements Controller {
    private final LeaderboardService leaderboardService;
    private final ObjectMapper objectMapper;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            // GET /api/leaderboard
            if (request.getMethod() == Method.GET && request.getPathParts().size() == 2) {
                return handleGetLeaderboard();
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"error\": \"Endpoint not found\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response handleGetLeaderboard() {
        try {
            List<LeaderboardEntryDTO> leaderboard = leaderboardService.getLeaderboard();

            return new Response(HttpStatus.OK, ContentType.JSON,
                    objectMapper.writeValueAsString(leaderboard));

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"An unexpected error occurred\"}");
        }
    }
}