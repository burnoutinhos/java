package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.model.TimeBlock;
import com.burnoutinhos.burnoutinhos_api.service.TimeBlockService;
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
 * Scaffold REST controller para a entidade TimeBlock.
 * Métodos intencionalmente vazios — apenas assinaturas e anotações.
 */
@RestController
@RequestMapping("/timeblocks")
@Tag(
    name = "TimeBlocks",
    description = "Endpoints para gerenciar blocos de tempo"
)
public class TimeBlockController {

    @Autowired
    private TimeBlockService service;

    @Operation(
        summary = "Criar time block",
        description = "Cria um novo bloco de tempo"
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
    public ResponseEntity<TimeBlock> create(@RequestBody TimeBlock timeBlock) {
        return null;
    }

    @Operation(
        summary = "Listar time blocks",
        description = "Retorna todos os blocos de tempo"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "Nenhum conteúdo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping
    public ResponseEntity<List<TimeBlock>> findAll() {
        return null;
    }

    @Operation(
        summary = "Buscar time block por ID",
        description = "Retorna um bloco de tempo pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TimeBlock> findById(@PathVariable Long id) {
        return null;
    }

    @Operation(
        summary = "Atualizar time block",
        description = "Atualiza um bloco de tempo existente"
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
    public ResponseEntity<TimeBlock> update(
        @PathVariable Long id,
        @RequestBody TimeBlock timeBlock
    ) {
        return null;
    }

    @Operation(
        summary = "Deletar time block",
        description = "Remove um bloco de tempo pelo seu ID"
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
