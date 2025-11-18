package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.config.AuthenticationUtil;
import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Suggestion;
import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.repository.SuggestionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scaffold de serviço para {@link Suggestion} com operações CRUD básicas.
 */
@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository repository;

    @Autowired
    private TodoService todoService;

    /**
     * Persiste uma entidade {@link Suggestion}.
     * Se o usuário não estiver presente, extrai o userId do token autenticado.
     */
    @Transactional
    public Suggestion save(Suggestion suggestion) {
        if (suggestion.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
            suggestion.setUser(user);
        }

        // Se a suggestion vier com um todo (ou ao menos o todo.id), carregue o Todo
        if (
            suggestion.getTodo() != null && suggestion.getTodo().getId() != null
        ) {
            Long todoId = suggestion.getTodo().getId();
            Todo todo = todoService.findById(todoId); // lança ResourceNotFoundException se não existir

            // seta a entidade gerenciada no lado Many
            suggestion.setTodo(todo);

            // Mantém a coleção do Todo atualizada (evita inconsistência no contexto de persistência)
            if (!todo.getSuggestions().contains(suggestion)) {
                todo.getSuggestions().add(suggestion);
            }
        }

        return repository.save(suggestion);
    }

    /**
     * Retorna todas as entidades {@link Suggestion}.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable("suggestions")
    public List<Suggestion> findAll() {
        return repository.findAll();
    }

    /**
     * Busca uma entidade {@link Suggestion} por id.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "suggestions", key = "#id")
    public Suggestion findById(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Suggestion not found")
            );
    }

    /**
     * Remove uma entidade {@link Suggestion} por id.
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Suggestion not found");
        }
        repository.deleteById(id);
    }

    /**
     * Atualiza uma entidade {@link Suggestion}.
     * Verifica existência e preenche o usuário a partir do token, se necessário.
     */
    @Transactional
    public Suggestion update(Suggestion suggestion) {
        Long id = suggestion.getId();
        if (id == null || !repository.existsById(id)) {
            throw new ResourceNotFoundException("Suggestion not found");
        }

        if (suggestion.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
            suggestion.setUser(user);
        }

        // Mesma lógica: se veio um todo.id, carrega e associa
        if (
            suggestion.getTodo() != null && suggestion.getTodo().getId() != null
        ) {
            Long todoId = suggestion.getTodo().getId();
            Todo todo = todoService.findById(todoId);
            suggestion.setTodo(todo);
            if (!todo.getSuggestions().contains(suggestion)) {
                todo.getSuggestions().add(suggestion);
            }
        }

        return repository.save(suggestion);
    }
}
