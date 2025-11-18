package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.Suggestion;
import com.burnoutinhos.burnoutinhos_api.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Suggestion resource.
 * Implements basic CRUD endpoints and validates request bodies using @Valid + BindingResult.
 */
@RestController
@RequestMapping("/suggestions")
@Tag(name = "Suggestions", description = "Endpoints para gerenciar sugestões")
public class SuggestionController {

    @Autowired
    private SuggestionService service;

    @Operation(
        summary = "Listar sugestões",
        description = "Retorna todas as sugestões"
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
                description = "Nenhuma sugestão encontrada"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @GetMapping
    public ResponseEntity<List<Suggestion>> findAll() {
        List<Suggestion> list = service.findAll();
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @Operation(
        summary = "Buscar sugestão por ID",
        description = "Retorna uma sugestão pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Sugestão encontrada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Sugestão não encontrada",
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
    public ResponseEntity<Suggestion> findById(@PathVariable Long id) {
        Suggestion s = service.findById(id);
        return ResponseEntity.ok(s);
    }

    @Operation(
        summary = "Atualizar sugestão",
        description = "Atualiza uma sugestão existente"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Sugestão atualizada com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida - dados de entrada incorretos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Sugestão não encontrada",
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
    public ResponseEntity<Suggestion> update(
        @PathVariable Long id,
        @Valid @RequestBody Suggestion suggestion,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Update suggestion not valid",
                bindingResult
            );
        }

        suggestion.setId(id);
        Suggestion updated = service.update(suggestion);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Deletar sugestão",
        description = "Remove uma sugestão pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "204",
                description = "Sugestão removida com sucesso"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Sugestão não encontrada",
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
