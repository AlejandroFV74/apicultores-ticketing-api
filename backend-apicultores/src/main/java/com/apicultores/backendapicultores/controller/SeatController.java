package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.domain.dto.response.SeatResponse;
import com.apicultores.backendapicultores.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<GeneralResponse> createSeat(@Valid @RequestBody CreateSeatRequest request) {
        SeatResponse response = seatService.createSeat(request);
        return buildResponse(
                "Seat created successfully",
                HttpStatus.CREATED,
                response
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> getAllSeats() {
        List<SeatResponse> seats = seatService.getAllSeats();
        return buildResponse(
                "Seats retrieved successfully",
                HttpStatus.OK,
                seats
        );
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<GeneralResponse> getSeatById(@PathVariable UUID seatId) {
        SeatResponse seat = seatService.getSeatById(seatId);
        return buildResponse(
                "Seat retrieved successfully",
                HttpStatus.OK,
                seat
        );
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> getSeatsByEventId(@PathVariable UUID eventId) {
        List<SeatResponse> seats = seatService.getSeatsByEventId(eventId);
        return buildResponse(
                "Seats retrieved successfully for event",
                HttpStatus.OK,
                seats
        );
    }

    @PutMapping("/{seatId}")
    public ResponseEntity<GeneralResponse> updateSeat(
            @PathVariable UUID seatId,
            @Valid @RequestBody UpdateSeatRequest request) {
        SeatResponse response = seatService.updateSeat(seatId, request);
        return buildResponse(
                "Seat updated successfully",
                HttpStatus.OK,
                response
        );
    }

    @DeleteMapping("/{seatId}")
    public ResponseEntity<GeneralResponse> deleteSeat(@PathVariable UUID seatId) {
        seatService.deleteSeat(seatId);
        return buildResponse(
                "Seat deleted successfully",
                HttpStatus.NO_CONTENT,
                null
        );
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build());
    }
}
