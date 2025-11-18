package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.model.dtos.TodoDTO;
import com.burnoutinhos.burnoutinhos_api.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Todo resource.
 * Implements basic CRUD endpoints and validates request DTOs using @Valid + BindingResult.
 */
@RestController
@RequestMapping("/todos")
@Tag(name = "Todos", description = "Endpoints para gerenciar tarefas (todos)")
public class TodoController {

    @Autowired
    private TodoService service;

    @Operation(
        summary = "Criar tarefa",
        description = "Cria uma nova tarefa (todo)"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "201",
                description = "Tarefa criada com sucesso",
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
    public ResponseEntity<Todo> create(
        @Valid @RequestBody TodoDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Create todo not valid",
                bindingResult
            );
        }

        Todo todo = new Todo();
        BeanUtils.copyProperties(dto, todo);

        Todo saved = service.save(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
        summary = "Listar tarefas",
        description = "Retorna todas as tarefas cadastradas"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de tarefas retornada com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "204",
                description = "Nenhuma tarefa encontrada"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @GetMapping
    public ResponseEntity<List<Todo>> findAll() {
        List<Todo> list = service.findAll();
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @Operation(
        summary = "Listar tarefas por usuário",
        description = "Retorna todas as tarefas cadastradas por um usuário específico"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de tarefas retornada com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "204",
                description = "Nenhuma tarefa encontrada"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Não autorizado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
        }
    )
    @GetMapping("/me")
    public ResponseEntity<Page<Todo>> findAllByUser(@AuthenticationPrincipal AppUser user, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<Todo> list = service.findAllByUser(user.getId(), page, size);

        return ResponseEntity.ok(list);
    }

    @Operation(
        summary = "Buscar tarefa por ID",
        description = "Retorna uma tarefa específica pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Tarefa encontrada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tarefa não encontrada",
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
    public ResponseEntity<Todo> findById(@PathVariable Long id) {
        Todo todo = service.findById(id);
        return ResponseEntity.ok(todo);
    }

    @Operation(
        summary = "Atualizar tarefa",
        description = "Atualiza uma tarefa existente"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "Tarefa atualizada com sucesso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requisição inválida - dados de entrada incorretos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tarefa não encontrada",
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
    public ResponseEntity<Todo> update(
        @PathVariable Long id,
        @Valid @RequestBody TodoDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Update todo not valid",
                bindingResult
            );
        }

        Todo updated = service.update(dto, id);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Deletar tarefa",
        description = "Remove uma tarefa pelo seu ID"
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "204",
                description = "Tarefa removida com sucesso"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Tarefa não encontrada",
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
