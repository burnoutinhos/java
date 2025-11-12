package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.model.Notification;
import com.burnoutinhos.burnoutinhos_api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Scaffold REST controller para a entidade Notification.
 * Métodos intencionalmente vazios — apenas assinaturas e anotações.
 */
@RestController
@RequestMapping("/notifications")
@Tag(
    name = "Notifications",
    description = "Endpoints para gerenciar notificações"
)
public class NotificationController {

    @Autowired
    private NotificationService service;

    @Operation(
        summary = "Criar notification",
        description = "Cria uma nova notificação"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "201",
                description = "Notificação criada"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida"
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @PostMapping
    public ResponseEntity<Notification> create(
        @RequestBody Notification notification
    ) {
        return null;
    }

    @Operation(
        summary = "Listar notifications",
        description = "Retorna todas as notificações"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Lista retornada com sucesso"
            ),
            @ApiResponse(responseCode = "204", description = "Nenhum conteúdo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping
    public ResponseEntity<List<Notification>> findAll() {
        return null;
    }

    @Operation(
        summary = "Buscar notification por ID",
        description = "Retorna uma notificação pelo ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Notificação encontrada"
            ),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Long id) {
        return null;
    }

    @Operation(
        summary = "Atualizar notification",
        description = "Atualiza uma notificação existente"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Notificação atualizada"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida"
            ),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Notification> update(
        @PathVariable Long id,
        @RequestBody Notification notification
    ) {
        return null;
    }

    @Operation(
        summary = "Remover notification",
        description = "Remove uma notificação pelo ID"
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
