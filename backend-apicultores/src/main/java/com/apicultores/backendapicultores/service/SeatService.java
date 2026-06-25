package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.dto.request.CreateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.response.SeatResponse;

import java.util.List;
import java.util.UUID;

public interface SeatService {

    SeatResponse createSeat(CreateSeatRequest request);

    List<SeatResponse> getAllSeats();

    SeatResponse getSeatById(UUID seatId);

    List<SeatResponse> getSeatsByEventId(UUID eventId);

    SeatResponse updateSeat(UUID seatId, UpdateSeatRequest request);

    void deleteSeat(UUID seatId);
}
