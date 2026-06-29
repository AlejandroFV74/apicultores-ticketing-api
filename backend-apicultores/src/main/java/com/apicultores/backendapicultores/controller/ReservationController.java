package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {
    private ReservationService reservationService;

    @PostMapping("/generate")
    public ResponseEntity<GeneralResponse> generateReservation(@RequestBody CreateReservationRequest resRequest){

        return buildResponse(
                "Reservación creada exitosamente",
                HttpStatus.CREATED,
                reservationService.createReservation(resRequest)
        );

    }

    @GetMapping("")
    public ResponseEntity<GeneralResponse> getAllReservations(){
        return buildResponse(
                "Listado de reservas",
                HttpStatus.OK,
                reservationService.getAllReservations()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteReservation(@PathVariable("id") UUID id){
        reservationService.deleteReservation(id);
        return buildResponse(
                "Reservación eliminada",
                HttpStatus.NO_CONTENT,
                null
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateReservation(@PathVariable("id") UUID id, @RequestBody UpdateReservationRequest request){
        return buildResponse(
                "Reservación actualizada",
                HttpStatus.OK,
                reservationService.updateReservation(id, request)
        );
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
