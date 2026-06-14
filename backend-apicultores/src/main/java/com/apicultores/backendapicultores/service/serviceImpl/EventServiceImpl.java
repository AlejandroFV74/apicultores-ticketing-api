package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.mappers.EventMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateEventRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateEventRequest;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.repository.EventRepository;
import com.apicultores.backendapicultores.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public EventResponse createEvent(CreateEventRequest request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        Event event = mapper.toEntity(request);
        event.setOrganizerId(userId);
        Event savedEvent = repository.save(event);
        return mapper.toDto(savedEvent);
    }

    @Override
    public List<EventResponse> getAllEvents() {
        List<Event> events = repository.findAll();

        if (events.isEmpty()) {
            throw new ResourceNotFoundException("No events found");
        }

        return events.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public EventResponse getEventById(UUID id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found")));
    }

    @Override
    public EventResponse updateEvent(UUID id, UpdateEventRequest request) {

        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return mapper.toDto(repository.save(mapper.toEntityUpdate(request, event)));
    }

    @Override
    public void deleteEvent(UUID id) {

        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        repository.delete(event);

    }

}
