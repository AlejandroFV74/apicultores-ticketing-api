package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.dto.request.CreateEventRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateEventRequest;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;

import java.util.List;
import java.util.UUID;

public interface EventService {
    EventResponse createEvent(CreateEventRequest request);

    List<EventResponse> getAllEvents();

    EventResponse getEventById(UUID id);

    EventResponse updateEvent(UUID id, UpdateEventRequest request);

    EventResponse deleteEvent(UUID id);

    List<EventResponse> getMyEvents();

    List<EventResponse> getManageEvents();

    List<EventResponse> searchByTitle(String title);

}
