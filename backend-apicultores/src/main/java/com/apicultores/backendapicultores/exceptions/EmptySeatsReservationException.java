package com.apicultores.backendapicultores.exceptions;

public class EmptySeatsReservationException extends RuntimeException {
    public EmptySeatsReservationException(String message) {
        super(message);
    }
}
