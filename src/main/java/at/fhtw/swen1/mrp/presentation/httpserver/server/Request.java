package at.fhtw.swen1.mrp.presentation.httpserver.server;


import at.fhtw.swen1.mrp.presentation.httpserver.http.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private Method method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String body;

    public Request() {
        this.pathParts = new ArrayList<>();
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public String getControllerRoute() {
        if (this.pathParts == null || this.pathParts.isEmpty()) {
            return null;
        }
        return '/' + this.pathParts.get(0) + '/' + this.pathParts.get(1);
    }

    public String getUrlContent() {
        return this.urlContent;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        boolean hasParams = urlContent.contains("?");

        if (hasParams) {
            String[] parts = urlContent.split("\\?", 2);
            this.setPathname(parts[0]);
            this.parseQueryParams(parts[1]);
        } else {
            this.setPathname(urlContent);
        }
    }

    private void parseQueryParams(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                this.queryParams.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part : stringParts) {
            if (part != null && !part.isEmpty()) {
                this.pathParts.add(part);
            }
        }
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getPathParts() {
        return pathParts;
    }

    public void setPathParts(List<String> pathParts) {
        this.pathParts = pathParts;
    }
}
