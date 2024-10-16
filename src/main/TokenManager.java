package main;

import java.util.HashMap;
import java.util.UUID;

public class TokenManager {
    private HashMap<String, String> tokens = new HashMap<>();

    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    public boolean isValidToken(String token) {
        return tokens.containsKey(token);
    }
}
