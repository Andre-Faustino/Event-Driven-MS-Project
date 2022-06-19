package com.microservices.kafka.admin.exception;

public class kafkaAdminException extends RuntimeException{

    public kafkaAdminException() {
    }

    public kafkaAdminException(String message) {
        super(message);
    }

    public kafkaAdminException(String message, Throwable cause) {
        super(message, cause);
    }
}
