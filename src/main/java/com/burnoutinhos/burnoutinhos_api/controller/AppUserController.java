package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.dtos.AuthResponseDTO;
import com.burnoutinhos.burnoutinhos_api.model.dtos.LoginDTO;
import com.burnoutinhos.burnoutinhos_api.model.dtos.RegisterAndUpdateUserDTO;
import com.burnoutinhos.burnoutinhos_api.service.AppUserService;
import com.burnoutinhos.burnoutinhos_api.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller para a entidade AppUser.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints para gerenciar usuários")
@Log4j2
public class AppUserController {

    @Autowired
    private AppUserService service;

    @Autowired
    private AuthenticationService authService;

    @Operation(
        summary = "Registrar usuário",
        description = "Registra um novo usuário no sistema e retorna um token JWT"
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
        @Valid @RequestBody RegisterAndUpdateUserDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Register user not valid",
                bindingResult
            );
        }

        log.info("register controller method dto {}", dto);
        AuthResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Login de usuário",
        description = "Autentica um usuário e retorna um token JWT"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Autenticado com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida - dados de entrada incorretos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Credenciais inválidas",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
        @Valid @RequestBody LoginDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Login not valid", bindingResult);
        }

        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Listar usuários",
        description = "Retorna todos os usuários cadastrados no sistema"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de usuários retornada com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "204",
                description = "Nenhum usuário encontrado"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )

    @GetMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(
        @AuthenticationPrincipal AppUser user,
        @RequestParam String password
    ) {
        boolean isValid = authService.verifyPassword(user, password);
        Map<String, Object> response = new HashMap<>();
        response.put("validPassword", isValid);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<AppUser>> findAll() {
        List<AppUser> users = service.findAll();
        if (users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "Buscar usuário por ID",
        description = "Retorna um usuário específico pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Usuário encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> findById(@PathVariable Long id) {
        AppUser user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Buscar usuário autenticado",
        description = "Retorna os dados do usuário atualmente autenticado"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Dados do usuário retornados",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @GetMapping("/me")
    public ResponseEntity<AppUser> findMe(
        @AuthenticationPrincipal AppUser user
    ) {
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza os dados de um usuário existente"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Usuário atualizado com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida - dados de entrada incorretos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @PutMapping
    public ResponseEntity<AuthResponseDTO> update(
        @Valid @RequestBody RegisterAndUpdateUserDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Update user not valid",
                bindingResult
            );
        }

        return ResponseEntity.ok(authService.updateAndRegister(dto));
    }

    @Operation(
        summary = "Deletar usuário",
        description = "Remove um usuário do sistema pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "204",
                description = "Usuário removido com sucesso"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
