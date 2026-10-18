package com.proyecto.rabbitadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.rabbitadmin.config.SecurityConfig;
import com.proyecto.rabbitadmin.dto.*;
import com.proyecto.rabbitadmin.service.RabbitAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RabbitAdminController.class)
@Import(SecurityConfig.class)
class RabbitAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RabbitAdminService rabbitAdminService;

    // ==========================================
    // PRUEBAS CON ROL ADMIN (200 / 201)
    // ==========================================

    @Test
    @WithMockUser(roles = "Admin")
    void testCreateQueue_Success() throws Exception {
        QueueRequestDTO request = new QueueRequestDTO();
        request.setName("demo.queue");
        request.setDurable(true);

        doNothing().when(rabbitAdminService).createQueue(any(QueueRequestDTO.class));

        mockMvc.perform(post("/api/rabbit/queues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testDeleteQueue_Success() throws Exception {
        when(rabbitAdminService.deleteQueue("demo.queue")).thenReturn(true);

        mockMvc.perform(delete("/api/rabbit/queues/demo.queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testCreateExchange_Success() throws Exception {
        ExchangeRequestDTO request = new ExchangeRequestDTO();
        request.setName("demo.exchange");
        request.setType("direct");

        doNothing().when(rabbitAdminService).createExchange(any(ExchangeRequestDTO.class));

        mockMvc.perform(post("/api/rabbit/exchanges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testCreateBinding_Success() throws Exception {
        BindingRequestDTO request = new BindingRequestDTO();
        request.setDestination("demo.queue");
        request.setDestinationType("QUEUE");
        request.setExchange("demo.exchange");
        request.setRoutingKey("demo.key");

        doNothing().when(rabbitAdminService).createBinding(any(BindingRequestDTO.class));

        mockMvc.perform(post("/api/rabbit/bindings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testPublishMessage_Success() throws Exception {
        PublishMessageDTO request = new PublishMessageDTO();
        request.setExchange("demo.exchange");
        request.setRoutingKey("demo.key");
        request.setBody("Mensaje de prueba");

        doNothing().when(rabbitAdminService).publishMessage(any(PublishMessageDTO.class));

        mockMvc.perform(post("/api/rabbit/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "Admin")
    void testCreateQueue_ValidationError() throws Exception {
        QueueRequestDTO request = new QueueRequestDTO();
        request.setName(""); // Blank -> validation error

        mockMvc.perform(post("/api/rabbit/queues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }

    // ==========================================
    // PRUEBAS DE SEGURIDAD (401 / 403)
    // ==========================================

    @Test
    @WithMockUser(roles = "User") // Rol no autorizado
    void testForbidden_WhenUserIsNotAdmin() throws Exception {
        QueueRequestDTO request = new QueueRequestDTO();
        request.setName("demo.queue");

        mockMvc.perform(post("/api/rabbit/queues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthorized_WhenNoToken() throws Exception {
        QueueRequestDTO request = new QueueRequestDTO();
        request.setName("demo.queue");

        mockMvc.perform(post("/api/rabbit/queues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}