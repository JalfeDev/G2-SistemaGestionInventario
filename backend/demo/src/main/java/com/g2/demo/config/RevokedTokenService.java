package com.g2.demo.config;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RevokedTokenService {

    private final Map<String, Long> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String token, long expirationEpochSeconds) {
        removeExpired();
        revokedTokens.put(token, expirationEpochSeconds);
    }

    public boolean isRevoked(String token) {
        removeExpired();
        return revokedTokens.containsKey(token);
    }

    private void removeExpired() {
        long now = Instant.now().getEpochSecond();
        revokedTokens.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
