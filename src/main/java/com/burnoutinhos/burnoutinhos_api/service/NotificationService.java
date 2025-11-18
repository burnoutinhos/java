package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.config.AuthenticationUtil;
import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Notification;
import com.burnoutinhos.burnoutinhos_api.repository.NotificationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scaffold de serviço para {@link Notification} com operações CRUD básicas.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    /**
     * Persiste uma entidade {@link Notification}.
     * Se o usuário não estiver presente na entidade, extrai o userId do token autenticado.
     */
    @Transactional
    public Notification save(Notification notification) {
        if (notification.getUser() == null) {
            AppUser user = AuthenticationUtil.extractUserFromToken();
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

    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "#userId + '_' + #page + '_' + #size")
    public Page<Notification> findAll(Long userId, Integer page, Integer size) {
        return repository.findByUserId(userId, PageRequest.of(page, size));
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
            AppUser user = AuthenticationUtil.extractUserFromToken();
            notification.setUser(user);
        }

        return repository.save(notification);
    }
}
