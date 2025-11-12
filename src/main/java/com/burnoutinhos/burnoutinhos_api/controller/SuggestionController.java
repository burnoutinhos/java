package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.model.Suggestion;
import com.burnoutinhos.burnoutinhos_api.service.SuggestionService;
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
 * Scaffold REST controller para a entidade Suggestion.
 * Métodos intencionalmente vazios — apenas assinaturas e anotações.
 */
@RestController
@RequestMapping("/suggestions")
@Tag(name = "Suggestions", description = "Endpoints para gerenciar sugestões")
public class SuggestionController {

    @Autowired
    private SuggestionService service;

    @Operation(
        summary = "Criar sugestão",
        description = "Cria uma nova sugestão"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "201", description = "Criado"),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida"
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @PostMapping
    public ResponseEntity<Suggestion> create(
        @RequestBody Suggestion suggestion
    ) {
        return null;
    }

    @Operation(
        summary = "Listar sugestões",
        description = "Retorna todas as sugestões"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "Nenhum conteúdo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping
    public ResponseEntity<List<Suggestion>> findAll() {
        return null;
    }

    @Operation(
        summary = "Buscar sugestão por ID",
        description = "Retorna uma sugestão pelo ID"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Suggestion> findById(@PathVariable Long id) {
        return null;
    }

    @Operation(
        summary = "Atualizar sugestão",
        description = "Atualiza uma sugestão existente"
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
    public ResponseEntity<Suggestion> update(
        @PathVariable Long id,
        @RequestBody Suggestion suggestion
    ) {
        return null;
    }

    @Operation(
        summary = "Remover sugestão",
        description = "Remove uma sugestão pelo ID"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "204", description = "Removido"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }
}
