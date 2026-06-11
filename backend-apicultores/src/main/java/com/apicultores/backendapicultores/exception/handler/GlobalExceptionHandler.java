package com.apicultores.backendapicultores.exception.handler;

import com.apicultores.backendapicultores.domain.dto.response.ApiErrorResponse;

import com.apicultores.backendapicultores.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({TicketNotFoundException.class, ReservationNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(Exception e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({TicketStatusException.class, ReservationStatusException.class})
    public ResponseEntity<ApiErrorResponse> handleStatusExceptions(Exception e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }


    @ExceptionHandler(EmptySeatsReservationException.class)
    public ResponseEntity<ApiErrorResponse> handleEmptySeatsException(EmptySeatsReservationException e) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor.");
    }


    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();

        return ResponseEntity
                .status(status)
                .body(ApiErrorResponse.builder()
                        .status(status.value())
                        .message(data)
                        .time(LocalDate.now())
                        .uri(uri)
                        .build()
                );
    }
}