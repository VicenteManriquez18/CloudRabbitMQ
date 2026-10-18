package com.proyecto.rabbitadmin.exception;

public class RabbitAdminException extends RuntimeException {

    private final int status;

    public RabbitAdminException(String message) {
        super(message);
        this.status = 400;
    }

    public RabbitAdminException(String message, int status) {
        super(message);
        this.status = status;
    }

    public RabbitAdminException(String message, Throwable cause) {
        super(message, cause);
        this.status = 500;
    }

    public int getStatus() {
        return status;
    }
}