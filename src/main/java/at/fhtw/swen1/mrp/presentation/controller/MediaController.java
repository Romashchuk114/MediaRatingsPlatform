package at.fhtw.swen1.mrp.presentation.controller;

import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;
import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Request;
import at.fhtw.swen1.mrp.presentation.httpserver.server.Response;
import at.fhtw.swen1.mrp.services.MediaService;


public class MediaController implements Controller  {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }
    @Override
    public Response handleRequest(Request request) {

        // POST /api/media - Create media
        if ("media".equals(request.getPathParts().get(1)) &&
                request.getMethod() == Method.POST) {
            return handleCreateMedia(request);
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON,
                "{\"error\": \"Endpoint not found\"}");
    }

    private Response handleCreateMedia(Request request) {
        return new Response(HttpStatus.OK, ContentType.JSON, """
                {
                   "title": "Inception",
                   "description": "Sci-fi thriller",
                   "mediaType": "movie",
                   "releaseYear": 2010,
                   "genres": [
                     "sci-fi",
                     "thriller"
                   ],
                   "ageRestriction": 12
                 }
                """);
    }

}
