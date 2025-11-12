package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.service.TodoService;
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
 * Scaffold REST controller para a entidade Todo.
 * Métodos intencionalmente vazios — apenas assinaturas e anotações.
 */
@RestController
@RequestMapping("/todos")
@Tag(name = "Todos", description = "Endpoints para gerenciar tarefas (todos)")
public class TodoController {

    @Autowired
    private TodoService service;

    @Operation(
        summary = "Criar todo",
        description = "Cria uma nova tarefa (Todo)."
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
    public ResponseEntity<Todo> create(@RequestBody Todo todo) {
        return null;
    }

    @Operation(
        summary = "Listar todos",
        description = "Retorna todas as tarefas (todos)."
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "Nenhum conteúdo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping
    public ResponseEntity<List<Todo>> findAll() {
        return null;
    }

    @Operation(
        summary = "Buscar todo por ID",
        description = "Retorna uma tarefa pelo seu ID."
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Todo> findById(@PathVariable Long id) {
        return null;
    }

    @Operation(
        summary = "Atualizar todo",
        description = "Atualiza uma tarefa existente."
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
    public ResponseEntity<Todo> update(
        @PathVariable Long id,
        @RequestBody Todo todo
    ) {
        return null;
    }

    @Operation(
        summary = "Deletar todo",
        description = "Remove uma tarefa pelo seu ID."
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
