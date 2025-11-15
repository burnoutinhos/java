package com.burnoutinhos.burnoutinhos_api.service;

import com.burnoutinhos.burnoutinhos_api.config.AuthenticationUtil;
import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.exceptions.UserAlreadyExistsException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.dtos.RegisterAndUpdateUserDTO;
import com.burnoutinhos.burnoutinhos_api.repository.AppUserRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsável pelo CRUD de usuários e por prover UserDetails para o Spring Security.
 *
 * Observação: responsabilidades de autenticação (login, emissão de token, uso de
 * AuthenticationManager) foram movidas para um serviço separado (ex: AuthenticationService).
 */
@Service
@Log4j2
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário a partir de DTO usado para registro/atualização.
     * NÃO realiza autenticação ou emissão de token — essa responsabilidade fica em outro serviço.
     *
     * @param dto DTO com os dados do usuário a ser criado
     * @return o usuário persistido
     * @throws UserAlreadyExistsException se o email já estiver em uso
     */
    @Transactional
    public AppUser createUser(RegisterAndUpdateUserDTO dto) {
        log.info("register dto: {} ", dto);

        if (repository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException(
                "This email is already in use"
            );
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        AppUser user = new AppUser();
        BeanUtils.copyProperties(dto, user);

        log.info("register user: {} ", user);

        return repository.saveAndFlush(user);
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

    /**
     * Atualiza os dados do usuário atualmente autenticado.
     * Utiliza utilitários de autenticação para extrair o usuário a ser atualizado.
     *
     * @param dto DTO com os novos dados
     * @return o usuário atualizado
     */
    @Transactional
    public AppUser update(RegisterAndUpdateUserDTO dto) {
        Long idFromUser = AuthenticationUtil.extractUserIdFromToken();

        AppUser user = AuthenticationUtil.extractUserFromToken();

        if (dto.getEmail() != null) {
            Optional<AppUser> byEmail = repository.findByEmail(dto.getEmail());
            if (
                byEmail.isPresent() && !byEmail.get().getId().equals(idFromUser)
            ) {
                throw new UserAlreadyExistsException(
                    "Email already in use by another user."
                );
            } else {
                user.setEmail(dto.getEmail());
            }
        }

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getLanguage() != null) {
            user.setLanguage(dto.getLanguage());
        }

        if (dto.getProfileImage() != null) {
            user.setProfileImage(dto.getProfileImage());
        }

        // Copia outros campos do DTO que possam existir (exceto id)
        BeanUtils.copyProperties(
            dto,
            user,
            "password",
            "email",
            "name",
            "language",
            "profileImage"
        );

        return repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        log.info("loadUserByUsername called with: {}", username);
        var opt = repository.findByEmail(username);
        log.info("repository.findByEmail result present: {}", opt.isPresent());
        return opt.orElseThrow(() ->
            new UsernameNotFoundException("User not exists.")
        );
    }

    // PasswordEncoder bean moved to SecurityBeansConfiguration to avoid circular dependencies.
}
