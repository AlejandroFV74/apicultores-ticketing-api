package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.common.enums.EventStatus;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;
import com.apicultores.backendapicultores.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EventService eventService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        List<EventResponse> allEvents = eventService.getAllEvents();
        long totalEvents = allEvents.size();

        long activeEvents = allEvents.stream()
                .filter(e -> e.getStatus() != null && EventStatus.ACTIVE.name().equals(e.getStatus().name()))
                .count();

        long draftEvents = allEvents.stream()
                .filter(e -> e.getStatus() != null && EventStatus.DRAFT.name().equals(e.getStatus().name()))
                .count();

        long cancelledEvents = allEvents.stream()
                .filter(e -> e.getStatus() != null && EventStatus.CANCELLED.name().equals(e.getStatus().name()))
                .count();

        long finishedEvents = allEvents.stream()
                .filter(e -> e.getStatus() != null && EventStatus.FINISHED.name().equals(e.getStatus().name()))
                .count();

        stats.put("totalEvents", totalEvents);
        stats.put("totalTickets", 0);
        stats.put("totalReservations", 0);
        stats.put("pendingReservations", 0);
        stats.put("activeEvents", activeEvents);
        stats.put("draftEvents", draftEvents);
        stats.put("cancelledEvents", cancelledEvents);
        stats.put("finishedEvents", finishedEvents);
        stats.put("upcomingEvents", activeEvents);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponse>> getRecentEvents(
            @RequestParam(defaultValue = "5") int limit) {
        List<EventResponse> events = eventService.getAllEvents();

        int size = events.size();
        int start = Math.max(0, size - limit);
        List<EventResponse> recentEvents = events.subList(start, size);

        return ResponseEntity.ok(recentEvents);
    }

    @GetMapping("/upcoming-events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents(
            @RequestParam(defaultValue = "5") int limit) {
        List<EventResponse> allEvents = eventService.getAllEvents();

        List<EventResponse> upcomingEvents = allEvents.stream()
                .filter(e -> e.getStatus() != null && EventStatus.ACTIVE.name().equals(e.getStatus().name()))
                .limit(limit)
                .collect(Collectors.toList());

        return ResponseEntity.ok(upcomingEvents);
    }

    @GetMapping("/events-by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, List<EventResponse>>> getEventsByStatus() {
        List<EventResponse> allEvents = eventService.getAllEvents();

        Map<String, List<EventResponse>> eventsByStatus = allEvents.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getStatus() != null ? e.getStatus().name() : "UNKNOWN"
                ));

        return ResponseEntity.ok(eventsByStatus);
    }
}