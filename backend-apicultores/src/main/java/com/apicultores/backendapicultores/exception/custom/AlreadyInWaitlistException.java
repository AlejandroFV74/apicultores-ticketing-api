package com.apicultores.backendapicultores.exception.custom;

public class AlreadyInWaitlistException extends RuntimeException {
    public AlreadyInWaitlistException(String message) {
        super(message);
    }
}