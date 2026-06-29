package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.service.serviceImpl.ReservationExpiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
//Clase solo para pruebas de expiración de reservas, no se expone en el frontend
public class AdminReservationController {

    private final ReservationExpiryService expiryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/expire-now")
    public ResponseEntity<GeneralResponse> expireNow(){
        var expired = expiryService.expireReservations();
        return buildResponse("Expired reservations executed", HttpStatus.OK, expired.size());
    }

    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build()
                );
    }
}
