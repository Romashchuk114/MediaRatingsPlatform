package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.business.User;
import at.fhtw.swen1.mrp.presentation.dto.AuthResponse;
import at.fhtw.swen1.mrp.presentation.dto.UserCredentialsRequest;
import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.TokenService;
import at.fhtw.swen1.mrp.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;


public class UserController implements Controller {
    private final UserService userService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            int pathSize = request.getPathParts().size();

            // /api/users/register
            if (pathSize == 3 && request.getPathParts().get(1).equals("users") &&
                    request.getPathParts().get(2).equals("register") &&
                    request.getMethod() == Method.POST) {
                return handleRegister(request);
            }

            // /api/users/login
            if (request.getPathParts().get(1).equals("users") &&
                    request.getPathParts().get(2).equals("login") &&
                    request.getMethod() == Method.POST) {
                return handleLogin(request);
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                    "{\"error\": \"Endpoint not found\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private Response handleRegister(Request request) {
        try {
            UserCredentialsRequest dto = objectMapper.readValue(request.getBody(), UserCredentialsRequest.class);

            if (!dto.isValid()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Invalid credentials\"}");
            }

            User user = userService.registerUser(dto.getUsername(), dto.getPassword());

            AuthResponse response = new AuthResponse(
                    "User registered successfully",
                    null,
                    user.getUsername()
            );

            return new Response(HttpStatus.CREATED, ContentType.JSON, objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request body\"}");
        }


    }

    private Response handleLogin(Request request) {
        try {
            UserCredentialsRequest dto = objectMapper.readValue(
                    request.getBody(), UserCredentialsRequest.class
            );

            if (!dto.isValid()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                        "{\"error\": \"Invalid credentials\"}");
            }

            Optional<User> userOpt = userService.loginUser(dto.getUsername(), dto.getPassword());

            if (userOpt.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"error\": \"Invalid credentials\"}");
            }
            String token = tokenService.generateToken(userOpt.get().getUsername(), userOpt.get().getId());

            AuthResponse response = new AuthResponse(
                    "Login successful",
                    token,
                    userOpt.get().getUsername()
            );

            return new Response(HttpStatus.OK, ContentType.JSON, objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request body\"}");
        }
    }
}
