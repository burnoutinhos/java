package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.config.AuthenticationUtil;
import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.model.Analytics;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.repository.AnalyticsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository repository;

    /**
     * Persiste uma entidade {@link Analytics}.
     * Se não houver usuário na entidade, extrai o userId do token autenticado.
     */
    @Transactional
    public Analytics save(Analytics analytics) {
        analytics.setUser(AuthenticationUtil.extractUserFromToken());
        return repository.save(analytics);
    }

    /**
     * Retorna todas as entidades {@link Analytics}.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable("analytics")
    public List<Analytics> findAll() {
        return repository.findAll();
    }

    /**
     * Busca uma entidade {@link Analytics} por id.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "analytics", key = "#id")
    public Analytics findById(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Analytics not found")
            );
    }

    /**
     * Remove uma entidade {@link Analytics} por id.
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Analytics not found");
        }
        repository.deleteById(id);
    }

    /**
     * Atualiza uma entidade {@link Analytics}.
     * Verifica existência e, se necessário, define o usuário a partir do token.
     */
    @Transactional
    public Analytics update(Analytics analytics) {
        Long id = analytics.getId();
        if (id == null || !repository.existsById(id)) {
            throw new ResourceNotFoundException("Analytics not found");
        }

        if (analytics.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
            analytics.setUser(user);
        }

        return repository.save(analytics);
    }
}
