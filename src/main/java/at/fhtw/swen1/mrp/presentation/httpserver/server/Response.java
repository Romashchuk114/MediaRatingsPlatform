package at.fhtw.swen1.mrp.presentation.httpserver.server;


import at.fhtw.swen1.mrp.presentation.httpserver.http.ContentType;
import at.fhtw.swen1.mrp.presentation.httpserver.http.HttpStatus;

public class Response {
    private HttpStatus status;
    private ContentType contentType;
    private String content;

    public Response(HttpStatus status, ContentType contentType, String content) {
        this.status = status;
        this.contentType = contentType;
        this.content = content;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public byte[] getContentBytes() {
        return content.getBytes();
    }
}