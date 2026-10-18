package com.proyecto.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class PublishMessageDTO {

    private String exchange = "";

    @NotBlank(message = "La routingKey es obligatoria")
    private String routingKey;

    @NotNull(message = "El cuerpo del mensaje no puede ser nulo")
    private String body;

    private Map<String, Object> headers;

    public PublishMessageDTO() {}

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Map<String, Object> getHeaders() { return headers; }
    public void setHeaders(Map<String, Object> headers) { this.headers = headers; }
}