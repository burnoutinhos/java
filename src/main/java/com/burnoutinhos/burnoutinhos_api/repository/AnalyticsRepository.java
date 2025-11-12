package com.burnoutinhos.burnoutinhos_api.repository;

import com.burnoutinhos.burnoutinhos_api.model.Analytics;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.enums.AnalyticsType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade {@link Analytics}.
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    List<Analytics> findByUser(AppUser user);

    List<Analytics> findByUserId(Long userId);

    List<Analytics> findByType(AnalyticsType type);

    List<Analytics> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Analytics> findTopByUserOrderByCreatedAtDesc(AppUser user);
}
