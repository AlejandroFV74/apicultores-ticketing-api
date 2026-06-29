package com.apicultores.backendapicultores.exception.custom;

public class PaymentStatusException extends RuntimeException {
    public PaymentStatusException(String message) {
        super(message);
    }
}
