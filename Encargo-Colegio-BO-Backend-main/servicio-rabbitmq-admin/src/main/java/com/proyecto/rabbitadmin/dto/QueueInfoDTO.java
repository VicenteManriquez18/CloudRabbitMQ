package com.proyecto.rabbitadmin.dto;

public class QueueInfoDTO {

    private String name;
    private long messages;
    private int consumers;
    private boolean durable;

    public QueueInfoDTO() {}

    public QueueInfoDTO(String name, long messages, int consumers, boolean durable) {
        this.name = name;
        this.messages = messages;
        this.consumers = consumers;
        this.durable = durable;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getMessages() { return messages; }
    public void setMessages(long messages) { this.messages = messages; }

    public int getConsumers() { return consumers; }
    public void setConsumers(int consumers) { this.consumers = consumers; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }
}