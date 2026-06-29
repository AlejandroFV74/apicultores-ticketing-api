package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.dto.request.CreateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.response.ReservationResponse;

import java.util.List;
import java.util.UUID;

public interface ReservationService {
    public ReservationResponse  createReservation(CreateReservationRequest request);
    List<ReservationResponse> getAllReservations();
    void deleteReservation(UUID reservationId);
    ReservationResponse updateReservation(UUID reservationId, UpdateReservationRequest request);
}
