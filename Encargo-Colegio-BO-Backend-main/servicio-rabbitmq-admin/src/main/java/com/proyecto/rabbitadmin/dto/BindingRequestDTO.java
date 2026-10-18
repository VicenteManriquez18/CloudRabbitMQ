package com.proyecto.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BindingRequestDTO {

    @NotBlank(message = "El destino es obligatorio")
    private String destination;

    @NotBlank(message = "El tipo de destino es obligatorio (QUEUE o EXCHANGE)")
    @Pattern(regexp = "^(QUEUE|EXCHANGE)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "destinationType debe ser QUEUE o EXCHANGE")
    private String destinationType = "QUEUE";

    @NotBlank(message = "El exchange de origen es obligatorio")
    private String exchange;

    private String routingKey = "";

    public BindingRequestDTO() {}

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDestinationType() { return destinationType; }
    public void setDestinationType(String destinationType) { this.destinationType = destinationType; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
}