package com.ni.la.oa.elearn.repo;

import com.ni.la.oa.elearn.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    long deleteByUser_Id(Long userId);                        // optional helper
    long deleteByExpiresAtBefore(Instant cutoff);             // optional cleanup
}
