package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.model.dtos.TodoDTO;
import com.burnoutinhos.burnoutinhos_api.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
 * REST controller for Todo resource.
 * Implements basic CRUD endpoints and validates request DTOs using @Valid + BindingResult.
 */
@RestController
@RequestMapping("/todos")
@Tag(name = "Todos", description = "Endpoints para gerenciar tarefas (todos)")
public class TodoController {

    @Autowired
    private TodoService service;

    @Operation(summary = "Create todo", description = "Creates a new Todo")
    @ApiResponses(
        {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
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

    @Operation(summary = "List todos", description = "Returns all Todos")
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
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
        summary = "Get todo by ID",
        description = "Returns a Todo by its ID"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Todo> findById(@PathVariable Long id) {
        Todo todo = service.findById(id);
        return ResponseEntity.ok(todo);
    }

    @Operation(
        summary = "Update todo",
        description = "Updates an existing Todo"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
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

    @Operation(summary = "Delete todo", description = "Deletes a Todo by ID")
    @ApiResponses(
        {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
