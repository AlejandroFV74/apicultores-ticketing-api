package com.apicultores.backendapicultores.exception.custom;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
