package com.burnoutinhos.burnoutinhos_api.repository;

import com.burnoutinhos.burnoutinhos_api.model.TimeBlock;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.model.enums.TimeBlockType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade {@link TimeBlock}.
 */
@Repository
public interface TimeBlockRepository extends JpaRepository<TimeBlock, Long> {

    List<TimeBlock> findByUser(AppUser user);

    List<TimeBlock> findByUserId(Long userId);

    List<TimeBlock> findByTodo(Todo todo);

    List<TimeBlock> findByTodoId(Long todoId);

    List<TimeBlock> findByType(TimeBlockType type);

    List<TimeBlock> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<TimeBlock> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<TimeBlock> findByNameContainingIgnoreCase(String text);

    Optional<TimeBlock> findTopByUserOrderByCreatedAtDesc(AppUser user);

    void deleteByTodoId(Long todoId);
}
