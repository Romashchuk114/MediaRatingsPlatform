package at.fhtw.swen1.mrp.presentation.httpserver.utils;


import at.fhtw.swen1.mrp.presentation.controller.Controller;
import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RequestHandler implements HttpHandler {
    private Router router;

    public RequestHandler(Router router) {
        this.router = router;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Request request = buildRequest(exchange);
            Response response = processRequest(request);
            sendResponse(exchange, response);
        } catch (Exception e) {
            Response errorResponse = new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{\"error\": \"Internal server error\"}"
            );
            sendResponse(exchange, errorResponse);
        } finally {
            exchange.close();
        }
    }

    private Request buildRequest(HttpExchange exchange) throws IOException {
        Request request = new Request();

        request.setMethod(Method.valueOf(exchange.getRequestMethod().toUpperCase()));
        request.setUrlContent(exchange.getRequestURI().toString());

        exchange.getRequestHeaders().forEach((key, values) -> {
            if (!values.isEmpty()) {
                request.addHeader(key.toLowerCase(), values.get(0));
            }
        });

        if (hasBody(request.getMethod())) {
            request.setBody(readBody(exchange));
        }

        return request;
    }

    private Response processRequest(Request request) {
        Controller controller = router.resolve(request.getControllerRoute());

        if (controller == null) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{\"error\": \"Route not found\"}"
            );
        }

        return controller.handleRequest(request);
    }

    private void sendResponse(HttpExchange exchange, Response response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", response.getContentType().type);
        exchange.getResponseHeaders().set("Connection", "close");

        byte[] responseBytes = response.getContentBytes();
        exchange.sendResponseHeaders(response.getStatus().code, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private boolean hasBody(Method method) {
        return method == Method.POST || method == Method.PUT;
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
