package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.config.AuthenticationUtil;
import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.model.dtos.TodoDTO;
import com.burnoutinhos.burnoutinhos_api.repository.TodoRepository;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço para {@link Todo} com operações CRUD básicas.
 * Implementação dos métodos usando repositório e utilitário de autenticação
 * para extrair o usuário do token quando necessário.
 */
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    @Autowired
    private SuggestionService suggestionService;

    /**
     * Persiste uma entidade {@link Todo}.
     * Se o usuário não estiver presente na entidade, extrai o userId do token autenticado.
     */
    @Transactional
    public Todo save(Todo todo) {
        if (todo.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
            todo.setUser(user);
        }
        return repository.save(todo);
    }

    /**
     * Retorna todas as entidades {@link Todo}.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable("todos")
    public List<Todo> findAll() {
        return repository.findAll();
    }

    /**
     * Busca uma entidade {@link Todo} por id.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "todos", key = "#id")
    public Todo findById(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
    }

    /**
     * Remove uma entidade {@link Todo} por id.
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Todo not found");
        }
        repository.deleteById(id);
    }

    /**
     * Atualiza uma entidade {@link Todo}.
     * Verifica existência e preenche o usuário a partir do token, se necessário.
     */
    @Transactional
    public Todo update(TodoDTO dto, Long id) {
        Todo todo = findById(id);
        BeanUtils.copyProperties(dto, todo);

        if (dto.getSuggestionId() != null) {
            todo.setSuggestion(
                suggestionService.findById(dto.getSuggestionId())
            );
        }

        if (id == null || !repository.existsById(id)) {
            throw new ResourceNotFoundException("Todo not found");
        }

        if (todo.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
            todo.setUser(user);
        }

        return repository.save(todo);
    }
}
