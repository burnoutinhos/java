package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.Suggestion;
import com.burnoutinhos.burnoutinhos_api.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
 * REST controller for Suggestion resource.
 * Implements basic CRUD endpoints and validates request bodies using @Valid + BindingResult.
 */
@RestController
@RequestMapping("/suggestions")
@Tag(name = "Suggestions", description = "Endpoints to manage suggestions")
public class SuggestionController {

    @Autowired
    private SuggestionService service;

    @Operation(
        summary = "Create suggestion",
        description = "Creates a new suggestion"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
        }
    )
    @PostMapping
    public ResponseEntity<Suggestion> create(
        @Valid @RequestBody Suggestion suggestion,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Create suggestion not valid",
                bindingResult
            );
        }

        Suggestion saved = service.save(suggestion);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
        summary = "List suggestions",
        description = "Returns all suggestions"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
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
        summary = "Get suggestion by ID",
        description = "Returns a suggestion by its ID"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Suggestion> findById(@PathVariable Long id) {
        Suggestion s = service.findById(id);
        return ResponseEntity.ok(s);
    }

    @Operation(
        summary = "Update suggestion",
        description = "Updates an existing suggestion"
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
        summary = "Delete suggestion",
        description = "Deletes a suggestion by ID"
    )
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
