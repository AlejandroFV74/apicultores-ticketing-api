package com.apicultores.backendapicultores.exception.custom;

public class WaitlistNotFoundException extends RuntimeException {
    public WaitlistNotFoundException(String message) {
        super(message);
    }
}