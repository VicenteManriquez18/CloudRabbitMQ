package com.proyecto.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;

public class QueueRequestDTO {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    private String name;

    private boolean durable = true;
    private boolean exclusive = false;
    private boolean autoDelete = false;

    private String deadLetterExchange;
    private String deadLetterRoutingKey;
    private Integer ttl;

    public QueueRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }

    public boolean isExclusive() { return exclusive; }
    public void setExclusive(boolean exclusive) { this.exclusive = exclusive; }

    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }

    public String getDeadLetterExchange() { return deadLetterExchange; }
    public void setDeadLetterExchange(String deadLetterExchange) { this.deadLetterExchange = deadLetterExchange; }

    public String getDeadLetterRoutingKey() { return deadLetterRoutingKey; }
    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) { this.deadLetterRoutingKey = deadLetterRoutingKey; }

    public Integer getTtl() { return ttl; }
    public void setTtl(Integer ttl) { this.ttl = ttl; }
}