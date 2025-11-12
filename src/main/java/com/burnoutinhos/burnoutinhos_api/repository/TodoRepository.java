package com.burnoutinhos.burnoutinhos_api.repository;

import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.model.enums.TodoType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade {@link Todo}.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUser(AppUser user);

    List<Todo> findByUserId(Long userId);

    List<Todo> findByUserAndIsCompleted(AppUser user, boolean isCompleted);

    List<Todo> findByIsCompleted(boolean isCompleted);

    List<Todo> findByType(TodoType type);

    List<Todo> findByNameContainingIgnoreCase(String text);

    List<Todo> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    List<Todo> findByEndDateBetween(LocalDateTime start, LocalDateTime end);

    List<Todo> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Todo> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Todo> findTopByUserOrderByCreatedAtDesc(AppUser user);

    void deleteByUserId(Long userId);

    Page<Todo> findByUser(AppUser user, Pageable pageable);

    Page<Todo> findByUserId(Long userId, Pageable pageable);

    Page<Todo> findByUserAndIsCompleted(
        AppUser user,
        boolean isCompleted,
        Pageable pageable
    );
}
