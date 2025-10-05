package at.fhtw.swen1.mrp.presentation.dto;

public class AuthResponse {
    private String message;
    private String token;       //TODO: anders machen!!!
    private String username;

    public AuthResponse() {
    }

    public AuthResponse(String message, String token, String username) {
        this.message = message;
        this.token = token;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
