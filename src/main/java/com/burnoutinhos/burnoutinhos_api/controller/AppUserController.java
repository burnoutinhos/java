package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Scaffold REST controller para a entidade AppUser.
 * Métodos intencionalmente vazios — apenas assinaturas e anotações.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints para gerenciar usuários")
public class AppUserController {

    @Autowired
    private AppUserService service;

    @Operation(
        summary = "Registrar usuário",
        description = "Registra um novo usuário"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida"
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @PostMapping
    public ResponseEntity<AppUser> register(@RequestBody AppUser user) {
        return null;
    }

    @Operation(
        summary = "Login de usuário",
        description = "Autentica um usuário"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Autenticado"),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Credenciais inválidas"
            ),
        }
    )
    @PostMapping
    public ResponseEntity<AppUser> login(@RequestBody AppUser user) {
        return null;
    }

    @Operation(
        summary = "Listar usuários",
        description = "Retorna todos os usuários"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "Nenhum conteúdo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping
    public ResponseEntity<List<AppUser>> findAll() {
        return null;
    }

    @Operation(
        summary = "Buscar usuário por ID",
        description = "Retorna um usuário pelo ID"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> findById(@PathVariable Long id) {
        return null;
    }

    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza os dados de um usuário existente"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Atualizado"),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida"
            ),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<AppUser> update(
        @PathVariable Long id,
        @RequestBody AppUser user
    ) {
        return null;
    }

    @Operation(
        summary = "Deletar usuário",
        description = "Remove um usuário pelo ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "204",
                description = "Removido com sucesso"
            ),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }
}
