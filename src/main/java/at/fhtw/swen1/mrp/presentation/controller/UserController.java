package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.UserService;


public class UserController implements Controller {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response handleRequest(Request request) {

        // /api/users/register
        if ("users".equals(request.getPathParts().get(1)) &&
                "register".equals(request.getPathParts().get(2)) &&
                request.getMethod() == Method.POST) {
            return handleRegister(request);
        }

        // /api/users/login
        if ("users".equals(request.getPathParts().get(1)) &&
                "login".equals(request.getPathParts().get(2)) &&
                request.getMethod() == Method.POST) {
            return handleLogin(request);
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                "{\"error\": \"Endpoint not found\"}");
}

    private Response handleRegister(Request request) {
        try {

            String responseJson = """
                    {
                      "username": "register",
                      "password": "pass123"
                    }
                    """;

            return new Response(HttpStatus.CREATED, ContentType.JSON, responseJson);

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request body\"}");
        }


    }

    private Response handleLogin(Request request) {
        try {

            String responseJson = """
                    {
                      "username": "login1",
                      "password": "pass123"
                    }
                    """;

            return new Response(HttpStatus.OK, ContentType.JSON, responseJson);

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"error\": \"Invalid request body\"}");
        }
    }
}
