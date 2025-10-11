package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.data.TokenRepository;

import java.util.Optional;
import java.util.UUID;

public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(String username, UUID userId) {
        String token = username + "-mrpToken";
        tokenRepository.save(token, userId);
        return token;
    }

    public Optional<UUID> validateToken(String token) {
        return tokenRepository.findUserIdByToken(token);
    }

    public void deleteToken(String token) {
        tokenRepository.delete(token);
    }

    public boolean isValidToken(String token) {
        return tokenRepository.exists(token);
    }
}
