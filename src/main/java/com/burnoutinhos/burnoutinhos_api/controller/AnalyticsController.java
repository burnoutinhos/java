package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.Analytics;
import com.burnoutinhos.burnoutinhos_api.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Analytics resource.
 * Implements basic CRUD endpoints and validates request bodies using @Valid + BindingResult.
 */
@RestController
@RequestMapping("/analytics")
@Tag(
    name = "Analytics",
    description = "Endpoints para gerenciar registros de analytics"
)
public class AnalyticsController {

    @Autowired
    private AnalyticsService service;

    @Operation(
        summary = "Criar analytics",
        description = "Cria um novo registro de analytics"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "201",
                description = "Registro criado com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida - dados de entrada incorretos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @PostMapping
    public ResponseEntity<Analytics> create(
        @Valid @RequestBody Analytics analytics,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Create analytics not valid",
                bindingResult
            );
        }

        Analytics saved = service.save(analytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
        summary = "Listar analytics",
        description = "Retorna todos os registros de analytics"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Lista retornada com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "204",
                description = "Nenhum registro encontrado"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @GetMapping
    public ResponseEntity<List<Analytics>> findAll() {
        List<Analytics> list = service.findAll();
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @Operation(
        summary = "Buscar analytics por ID",
        description = "Retorna um registro de analytics pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Registro encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Registro não encontrado",
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
    public ResponseEntity<Analytics> findById(@PathVariable Long id) {
        Analytics a = service.findById(id);
        return ResponseEntity.ok(a);
    }

    @Operation(
        summary = "Atualizar analytics",
        description = "Atualiza um registro de analytics existente"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Registro atualizado com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida - dados de entrada incorretos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Registro não encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Analytics> update(
        @PathVariable Long id,
        @Valid @RequestBody Analytics analytics,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Update analytics not valid",
                bindingResult
            );
        }

        analytics.setId(id);
        Analytics updated = service.update(analytics);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Deletar analytics",
        description = "Remove um registro de analytics pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "204",
                description = "Registro removido com sucesso"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Registro não encontrado",
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
