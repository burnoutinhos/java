package com.burnoutinhos.burnoutinhos_api.repository;

import com.burnoutinhos.burnoutinhos_api.model.Suggestion;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade {@link Suggestion}.
 */
@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    List<Suggestion> findByUser(AppUser user);

    List<Suggestion> findByUserId(Long userId);

    Page<Suggestion> findByUserId(Long userId, Pageable pageable);

    List<Suggestion> findBySuggestionContainingIgnoreCase(String text);

    List<Suggestion> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Suggestion> findTopByUserOrderByCreatedAtDesc(AppUser user);
}
