package com.apicultores.backendapicultores.exception.custom;

public class EmptySeatsReservationException extends RuntimeException {
    public EmptySeatsReservationException(String message) {
        super(message);
    }
}
