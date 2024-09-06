package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository  extends JpaRepository<BlacklistedToken , Long> {
    Optional<BlacklistedToken> findByToken(String token);
    void deleteByExpiresAtBefore(Instant now);
}
