package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.exceptions.UserAlreadyExistsException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.repository.AppUserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repository;

    @Transactional
    public AppUser save(AppUser user) {
        // Verifica se já existe usuário com o mesmo email
        if (
            user.getEmail() != null && repository.existsByEmail(user.getEmail())
        ) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        // Se houver senha, codifica antes de persistir
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder().encode(user.getPassword()));
        }

        return repository.save(user);
    }

    @Transactional(readOnly = true)
    @Cacheable("appUsers")
    public List<AppUser> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "appUsers", key = "#id")
    public AppUser findById(Long id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public AppUser update(AppUser user) {
        Long id = user.getId();
        if (id == null || !repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }

        // Se email mudou para um que já existe em outro usuário, impedir
        if (user.getEmail() != null) {
            Optional<AppUser> byEmail = repository.findByEmail(user.getEmail());
            if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
                throw new UserAlreadyExistsException(
                    "Email already in use by another user."
                );
            }
        }

        // Se senha foi fornecida, codificar
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder().encode(user.getPassword()));
        } else {
            // preservar a senha existente se não foi informada no update
            AppUser existing = repository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
                );
            user.setPassword(existing.getPassword());
        }

        return repository.save(user);
    }

    @Override
    public AppUser loadUserByUsername(String username)
        throws UsernameNotFoundException {
        return repository
            .findByEmail(username)
            .orElseThrow(() ->
                new ResourceNotFoundException("User not exists.")
            );
    }

    @Bean
    UserDetailsService generateUser() {
        return username -> {
            AppUser usuario = repository
                .findByEmail(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User Not Found")
                );

            return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles("USER")
                .build();
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
