package com.apicultores.backendapicultores.exception.custom;

public class LimitSeatsException extends RuntimeException {
    public LimitSeatsException(String message) {
        super(message);
    }
}
