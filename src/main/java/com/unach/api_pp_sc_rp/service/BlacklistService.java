package com.unach.api_pp_sc_rp.service;

import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;

public interface BlacklistService {
    void blacklistToken(String token, Instant expiresAt);

    boolean isTokenBlacklisted(String token);

    @Scheduled(fixedRate = 3600000) // Cada hora
    void cleanupExpiredTokens();
}
