package com.burnoutinhos.burnoutinhos_api.controller;

import com.burnoutinhos.burnoutinhos_api.exceptions.BadRequestException;
import com.burnoutinhos.burnoutinhos_api.model.Notification;
import com.burnoutinhos.burnoutinhos_api.service.NotificationService;
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
 * REST controller for Notification resource.
 * Implements basic CRUD endpoints and validates request bodies using @Valid + BindingResult.
 */
@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Endpoints to manage notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @Operation(
        summary = "Create notification",
        description = "Creates a new notification"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
        }
    )
    @PostMapping
    public ResponseEntity<Notification> create(
        @Valid @RequestBody Notification notification,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Create notification not valid",
                bindingResult
            );
        }

        Notification saved = service.save(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
        summary = "List notifications",
        description = "Returns all notifications"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
        }
    )
    @GetMapping
    public ResponseEntity<List<Notification>> findAll() {
        List<Notification> list = service.findAll();
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @Operation(
        summary = "Get notification by ID",
        description = "Returns a notification by its ID"
    )
    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Long id) {
        Notification n = service.findById(id);
        return ResponseEntity.ok(n);
    }

    @Operation(
        summary = "Update notification",
        description = "Updates an existing notification"
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
    public ResponseEntity<Notification> update(
        @PathVariable Long id,
        @Valid @RequestBody Notification notification,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(
                "Update notification not valid",
                bindingResult
            );
        }

        notification.setId(id);
        Notification updated = service.update(notification);
        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Delete notification",
        description = "Deletes a notification by ID"
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
