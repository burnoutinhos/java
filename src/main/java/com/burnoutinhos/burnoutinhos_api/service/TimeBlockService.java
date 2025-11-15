package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.config.AuthenticationUtil;
import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.TimeBlock;
import com.burnoutinhos.burnoutinhos_api.repository.TimeBlockRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scaffold de serviço para {@link TimeBlock} com operações CRUD básicas.
 * Implementação dos métodos usando repositório e utilitário de autenticação
 * para extrair o usuário do token quando necessário.
 */
@Service
public class TimeBlockService {

    @Autowired
    private TimeBlockRepository repository;

    /**
     * Persiste uma entidade {@link TimeBlock}.
     * Se o usuário não estiver presente na entidade, extrai o userId do token autenticado.
     */
    @Transactional
    public TimeBlock save(TimeBlock timeBlock) {
        if (timeBlock.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
            timeBlock.setUser(user);
        }
        return repository.save(timeBlock);
    }

    /**
     * Retorna todas as entidades {@link TimeBlock}.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable("timeBlocks")
    public List<TimeBlock> findAll() {
        return repository.findAll();
    }

    /**
     * Busca uma entidade {@link TimeBlock} por id.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "timeBlocks", key = "#id")
    public TimeBlock findById(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("TimeBlock not found")
            );
    }

    /**
     * Remove uma entidade {@link TimeBlock} por id.
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("TimeBlock not found");
        }
        repository.deleteById(id);
    }

    /**
     * Atualiza uma entidade {@link TimeBlock}.
     * Verifica existência e, se necessário, define o usuário a partir do token.
     */
    @Transactional
    public TimeBlock update(TimeBlock timeBlock) {
        Long id = timeBlock.getId();
        if (id == null || !repository.existsById(id)) {
            throw new ResourceNotFoundException("TimeBlock not found");
        }

        if (timeBlock.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
            timeBlock.setUser(user);
        }

        return repository.save(timeBlock);
    }
}
