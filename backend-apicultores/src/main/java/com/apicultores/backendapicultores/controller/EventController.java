package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.CreateEventRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateEventRequest;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;
import com.apicultores.backendapicultores.domain.dto.response.EventReportResponse;
import com.apicultores.backendapicultores.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody CreateEventRequest request) {

        EventResponse response = service.createEvent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAll() {

        List<EventResponse> events = service.getAllEvents();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable UUID id) {

        EventResponse event = service.getEventById(id);

        return ResponseEntity.ok(event);
    }


    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping("/my-events")
    public ResponseEntity<List<EventResponse>> getMyEvents() {
        return ResponseEntity.ok(service.getMyEvents());
    }


    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchByTitle(
            @RequestParam String title) {
        return ResponseEntity.ok(service.searchByTitle(title));
    }


    @PreAuthorize("hasRole('ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request) {

        EventResponse updated = service.updateEvent(id, request);

        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EventResponse> delete(@PathVariable UUID id) {

        service.deleteEvent(id);

        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    @GetMapping("/{id}/report")
    public ResponseEntity<EventReportResponse> getReport(@PathVariable UUID id) {

        return ResponseEntity.ok(service.getEventReport(id));
    }

}
