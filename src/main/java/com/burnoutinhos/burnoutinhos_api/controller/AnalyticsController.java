package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.Analytics;
import com.burnoutinhos.burnoutinhos_api.service.AnalyticsService;
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
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Analytics resource.
 * Implements basic CRUD endpoints and validates request bodies using @Valid + BindingResult.
 */
@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics", description = "Endpoints to manage analytics records")
public class AnalyticsController {

    @Autowired
    private AnalyticsService service;

    @Operation(
        summary = "Create analytics",
        description = "Creates a new Analytics record."
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
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
        summary = "List analytics",
        description = "Returns all analytics records."
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No content"),
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
        summary = "Get analytics by ID",
        description = "Returns an Analytics record by its ID."
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Analytics> findById(@PathVariable Long id) {
        Analytics a = service.findById(id);
        return ResponseEntity.ok(a);
    }

    @Operation(
        summary = "Update analytics",
        description = "Updates an existing Analytics record."
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
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
        summary = "Delete analytics",
        description = "Deletes an Analytics record by ID."
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found"),
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
