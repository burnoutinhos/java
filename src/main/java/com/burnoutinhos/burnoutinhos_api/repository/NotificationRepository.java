package com.burnoutinhos.burnoutinhos_api.repository;

import com.burnoutinhos.burnoutinhos_api.model.Notification;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade {@link Notification}.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(AppUser user);

    List<Notification> findByUserId(Long userId);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Notification> findTopByUserOrderByCreatedAtDesc(AppUser user);

    List<Notification> findByMessageContainingIgnoreCase(String text);
}
