package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.dto.request.CreateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.response.ReservationResponse;

public interface ReservationService {
    public ReservationResponse  createReservation(CreateReservationRequest request);
}
