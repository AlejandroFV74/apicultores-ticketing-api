package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.common.enums.EventStatus;
import com.apicultores.backendapicultores.domain.dto.request.CreateEventRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateEventRequest;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;
import com.apicultores.backendapicultores.domain.entity.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event toEntity(CreateEventRequest request) {
        return Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .venue(request.getVenue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(
                        request.getStatus() != null
                                ? request.getStatus()
                                : EventStatus.DRAFT
                )
                .maxTicketsPerUser(
                        request.getMaxTicketsPerUser() != null
                                ? request.getMaxTicketsPerUser()
                                : 5
                )
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Event toEntityUpdate(UpdateEventRequest request, Event event) {

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (request.getStatus() != null) event.setStatus(request.getStatus());
        if (request.getMaxTicketsPerUser() != null) event.setMaxTicketsPerUser(request.getMaxTicketsPerUser());

        return event;
    }

    public EventResponse toDto(Event event) {
        return EventResponse.builder()
                .eventId(event.getEventId())
                .organizerId(event.getOrganizerId())
                .title(event.getTitle())
                .description(event.getDescription())
                .venue(event.getVenue())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .status(event.getStatus())
                .maxTicketsPerUser(event.getMaxTicketsPerUser())
                .createdAt(event.getCreatedAt())
                .build();
    }

}
