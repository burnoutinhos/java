package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.TimeBlock;
import com.burnoutinhos.burnoutinhos_api.model.dtos.TimeBlockDTO;
import com.burnoutinhos.burnoutinhos_api.service.TimeBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.BeanUtils;
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
 * Controller REST para TimeBlock com validação e tratamento de BindingResult.
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
            @ApiResponse(
                responseCode = "201",
                description = "Bloco de tempo criado com sucesso",
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
    public ResponseEntity<TimeBlock> create(
        @Valid @RequestBody TimeBlockDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Create TimeBlock not valid",
                bindingResult
            );
        }

        TimeBlock timeBlock = new TimeBlock();
        BeanUtils.copyProperties(dto, timeBlock);

        TimeBlock saved = service.save(timeBlock);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
        summary = "Listar time blocks",
        description = "Retorna todos os blocos de tempo"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de blocos de tempo retornada com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "204",
                description = "Nenhum bloco de tempo encontrado"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @GetMapping
    public ResponseEntity<List<TimeBlock>> findAll() {
        List<TimeBlock> list = service.findAll();
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @Operation(
        summary = "Buscar time block por ID",
        description = "Retorna um bloco de tempo pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Bloco de tempo encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Bloco de tempo não encontrado",
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
    public ResponseEntity<TimeBlock> findById(@PathVariable Long id) {
        TimeBlock tb = service.findById(id);
        return ResponseEntity.ok(tb);
    }

    @Operation(
        summary = "Atualizar time block",
        description = "Atualiza um bloco de tempo existente"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Bloco de tempo atualizado com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida - dados de entrada incorretos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Bloco de tempo não encontrado",
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
    public ResponseEntity<TimeBlock> update(
        @PathVariable Long id,
        @Valid @RequestBody TimeBlockDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Update TimeBlock not valid",
                bindingResult
            );
        }

        TimeBlock timeBlock = new TimeBlock();
        BeanUtils.copyProperties(dto, timeBlock);
        timeBlock.setId(id);

        TimeBlock updated = service.update(timeBlock);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Deletar time block",
        description = "Remove um bloco de tempo pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "204",
                description = "Bloco de tempo removido com sucesso"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Bloco de tempo não encontrado",
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
