package com.burnoutinhos.burnoutinhos_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.burnoutinhos.burnoutinhos_api.exceptions.ResourceNotFoundException;
import com.burnoutinhos.burnoutinhos_api.model.Notification;
import com.burnoutinhos.burnoutinhos_api.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes de integração para o NotificationController.
 * Usa @SpringBootTest com @AutoConfigureMockMvc (addFilters = false) para desabilitar filtros de segurança.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    private Notification notification;
    private Notification notificationWithId;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setMessage("Test notification message");

        notificationWithId = new Notification();
        notificationWithId.setId(1L);
        notificationWithId.setMessage("Test notification message");
        notificationWithId.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName(
        "POST /notifications - Should create notification successfully"
    )
    void testCreateNotification_Success() throws Exception {
        // Given
        when(notificationService.save(any(Notification.class))).thenReturn(
            notificationWithId
        );

        // When & Then
        mockMvc
            .perform(
                post("/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(notification))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(
                jsonPath("$.message").value("Test notification message")
            );

        verify(notificationService, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("GET /notifications - Should return all notifications")
    void testFindAllNotifications_Success() throws Exception {
        // Given
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setMessage("Second notification");
        notification2.setCreatedAt(LocalDateTime.now());

        List<Notification> notifications = Arrays.asList(
            notificationWithId,
            notification2
        );

        when(notificationService.findAll()).thenReturn(notifications);

        // When & Then
        mockMvc
            .perform(get("/notifications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(
                jsonPath("$[0].message").value("Test notification message")
            )
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].message").value("Second notification"));

        verify(notificationService, times(1)).findAll();
    }

    @Test
    @DisplayName(
        "GET /notifications - Should return 200 when no notifications found"
    )
    void testFindAllNotifications_NoContent() throws Exception {
        // Given
        when(notificationService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc
            .perform(get("/notifications"))
            .andExpect(status().isOk());

        verify(notificationService, times(1)).findAll();
    }

    @Test
    @DisplayName(
        "GET /notifications - Should return 200 when notifications list is null"
    )
    void testFindAllNotifications_NullList() throws Exception {
        // Given
        when(notificationService.findAll()).thenReturn(null);

        // When & Then
        mockMvc
            .perform(get("/notifications"))
            .andExpect(status().isOk());

        verify(notificationService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /notifications/{id} - Should return notification by id")
    void testFindNotificationById_Success() throws Exception {
        // Given
        when(notificationService.findById(1L)).thenReturn(notificationWithId);

        // When & Then
        mockMvc
            .perform(get("/notifications/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(
                jsonPath("$.message").value("Test notification message")
            );

        verify(notificationService, times(1)).findById(1L);
    }

    @Test
    @DisplayName(
        "GET /notifications/{id} - Should return 404 when notification not found"
    )
    void testFindNotificationById_NotFound() throws Exception {
        // Given
        when(notificationService.findById(anyLong())).thenThrow(
            new ResourceNotFoundException("Notification not found")
        );

        // When & Then
        mockMvc
            .perform(get("/notifications/999"))
            .andExpect(status().isNotFound());

        verify(notificationService, times(1)).findById(999L);
    }

    @Test
    @DisplayName(
        "PUT /notifications/{id} - Should update notification successfully"
    )
    void testUpdateNotification_Success() throws Exception {
        // Given
        Notification updatedNotification = new Notification();
        updatedNotification.setId(1L);
        updatedNotification.setMessage("Updated notification message");
        updatedNotification.setCreatedAt(LocalDateTime.now());

        when(notificationService.update(any(Notification.class))).thenReturn(
            updatedNotification
        );

        // When & Then
        mockMvc
            .perform(
                put("/notifications/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(notification))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(
                jsonPath("$.message").value("Updated notification message")
            );

        verify(notificationService, times(1)).update(any(Notification.class));
    }

    @Test
    @DisplayName(
        "PUT /notifications/{id} - Should return 404 when notification not found"
    )
    void testUpdateNotification_NotFound() throws Exception {
        // Given
        when(notificationService.update(any(Notification.class))).thenThrow(
            new ResourceNotFoundException("Notification not found")
        );

        // When & Then
        mockMvc
            .perform(
                put("/notifications/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(notification))
            )
            .andExpect(status().isNotFound());

        verify(notificationService, times(1)).update(any(Notification.class));
    }

    @Test
    @DisplayName(
        "DELETE /notifications/{id} - Should delete notification successfully"
    )
    void testDeleteNotification_Success() throws Exception {
        // Given
        doNothing().when(notificationService).delete(1L);

        // When & Then
        mockMvc
            .perform(delete("/notifications/1"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));

        verify(notificationService, times(1)).delete(1L);
    }

    @Test
    @DisplayName(
        "DELETE /notifications/{id} - Should return 404 when notification not found"
    )
    void testDeleteNotification_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Notification not found"))
            .when(notificationService)
            .delete(anyLong());

        // When & Then
        mockMvc
            .perform(delete("/notifications/999"))
            .andExpect(status().isNotFound());

        verify(notificationService, times(1)).delete(999L);
    }
}
