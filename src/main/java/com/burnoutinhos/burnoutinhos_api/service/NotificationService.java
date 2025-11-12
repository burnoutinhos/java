package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.config.AuthenticationUtil;
import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Notification;
import com.burnoutinhos.burnoutinhos_api.repository.AppUserRepository;
import com.burnoutinhos.burnoutinhos_api.repository.NotificationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scaffold de serviço para {@link Notification} com operações CRUD básicas.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * Persiste uma entidade {@link Notification}.
     * Se o usuário não estiver presente na entidade, extrai o userId do token autenticado.
     */
    @Transactional
    public Notification save(Notification notification) {
        if (notification.getUser() == null) {
            Long userId = AuthenticationUtil.extractUserIdFromToken();
            AppUser user = appUserRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
                );
            notification.setUser(user);
        }
        return repository.save(notification);
    }

    /**
     * Retorna todas as entidades {@link Notification}.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable("notifications")
    public List<Notification> findAll() {
        return repository.findAll();
    }

    /**
     * Busca uma entidade {@link Notification} por id.
     * Cacheável e com transação somente leitura.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "#id")
    public Notification findById(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Notification not found")
            );
    }

    /**
     * Remove uma entidade {@link Notification} por id.
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found");
        }
        repository.deleteById(id);
    }

    /**
     * Atualiza uma entidade {@link Notification}.
     * Verifica existência e preenche o usuário a partir do token, se necessário.
     */
    @Transactional
    public Notification update(Notification notification) {
        Long id = notification.getId();
        if (id == null || !repository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found");
        }

        if (notification.getUser() == null) {
            Long userId = AuthenticationUtil.extractUserIdFromToken();
            AppUser user = appUserRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
                );
            notification.setUser(user);
        }

        return repository.save(notification);
    }
}
