package com.proyecto.rabbitadmin.controller;

import com.proyecto.rabbitadmin.dto.*;
import com.proyecto.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rabbit")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    // ==========================================
    // COLAS
    // ==========================================

    @PostMapping("/queues")
    public ResponseEntity<ApiResponseDTO<Void>> createQueue(@Valid @RequestBody QueueRequestDTO request) {
        rabbitAdminService.createQueue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDTO<>(true, "Cola '" + request.getName() + "' creada exitosamente.")
        );
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteQueue(@PathVariable String name) {
        boolean deleted = rabbitAdminService.deleteQueue(name);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Cola '" + name + "' eliminada exitosamente (resultado: " + deleted + ").")
        );
    }

    @GetMapping("/queues")
    public ResponseEntity<ApiResponseDTO<List<QueueInfoDTO>>> getQueues() {
        List<QueueInfoDTO> queues = rabbitAdminService.getQueues();
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Lista de colas obtenida exitosamente.", queues)
        );
    }

    // ==========================================
    // EXCHANGES
    // ==========================================

    @PostMapping("/exchanges")
    public ResponseEntity<ApiResponseDTO<Void>> createExchange(@Valid @RequestBody ExchangeRequestDTO request) {
        rabbitAdminService.createExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDTO<>(true, "Exchange '" + request.getName() + "' de tipo '" + request.getType() + "' creado exitosamente.")
        );
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteExchange(@PathVariable String name) {
        boolean deleted = rabbitAdminService.deleteExchange(name);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Exchange '" + name + "' eliminado exitosamente (resultado: " + deleted + ").")
        );
    }

    // ==========================================
    // BINDINGS
    // ==========================================

    @PostMapping("/bindings")
    public ResponseEntity<ApiResponseDTO<Void>> createBinding(@Valid @RequestBody BindingRequestDTO request) {
        rabbitAdminService.createBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDTO<>(true, "Enlace creado entre exchange '" + request.getExchange() + "' y " + request.getDestinationType().toLowerCase() + " '" + request.getDestination() + "' con routingKey '" + request.getRoutingKey() + "'.")
        );
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<ApiResponseDTO<Void>> deleteBinding(@Valid @RequestBody BindingRequestDTO request) {
        rabbitAdminService.deleteBinding(request);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Enlace eliminado exitosamente.")
        );
    }

    // ==========================================
    // PUBLICAR MENSAJE
    // ==========================================

    @PostMapping("/publish")
    public ResponseEntity<ApiResponseDTO<Void>> publishMessage(@Valid @RequestBody PublishMessageDTO request) {
        rabbitAdminService.publishMessage(request);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Mensaje publicado exitosamente en exchange '" + request.getExchange() + "' con routingKey '" + request.getRoutingKey() + "'.")
        );
    }
}