package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.mappers.EventMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateEventRequest;
import com.apicultores.backendapicultores.domain.dto.request.SeatConfigurationRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateEventRequest;
import com.apicultores.backendapicultores.domain.dto.response.EventReportResponse;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;
import com.apicultores.backendapicultores.domain.dto.response.SeatReportDTO;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.repository.EventRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventMapper mapper;
    private final CurrentUserProvider currentUserProvider;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas");
        }

        if (request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new BadRequestException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (request.getSeats() == null || request.getSeats().isEmpty()) {
            throw new BadRequestException("Debe incluir al menos una configuración de asientos");
        }

        UUID userId = currentUserProvider.getCurrentUserId();

        Event event = mapper.toEntity(request);
        event.setOrganizerId(userId);

        Event savedEvent = repository.save(event);

        List<Seat> seats = new ArrayList<>();

        for (SeatConfigurationRequest seatConfig : request.getSeats()) {

            if (seatConfig.getQuantity() == null || seatConfig.getQuantity() <= 0) {
                throw new BadRequestException("La cantidad de asientos debe ser mayor a 0");
            }

            if (seatConfig.getPrice() == null || seatConfig.getPrice() < 0) {
                throw new BadRequestException("El precio no puede ser negativo");
            }

            if (seatConfig.getSeatType() == null) {
                throw new BadRequestException("El tipo de asiento es obligatorio");
            }

            for (int i = 1; i <= seatConfig.getQuantity(); i++) {

                Seat seat = Seat.builder()
                        .eventId(savedEvent.getEventId())
                        .seatNumber(seatConfig.getSeatType().name() + "-" + i)
                        .seatType(seatConfig.getSeatType())
                        .price(seatConfig.getPrice())
                        .status(SeatStatus.AVAILABLE)
                        .createdAt(LocalDateTime.now())
                        .build();

                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);

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

        UUID userId = currentUserProvider.getCurrentUserId();

        if (!event.getOrganizerId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para modificar este evento");
        }

        return mapper.toDto(repository.save(mapper.toEntityUpdate(request, event)));
    }

    @Override
    public void deleteEvent(UUID id) {

        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        UUID userId = currentUserProvider.getCurrentUserId();

        if (!event.getOrganizerId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para eliminar este evento");
        }

        repository.delete(event);
    }

    @Override
    public List<EventResponse> getMyEvents() {

        UUID userId = currentUserProvider.getCurrentUserId();

        List<Event> events = repository.findByOrganizerId(userId);

        if (events.isEmpty()) {
            throw new ResourceNotFoundException("No events found for this organizer");
        }

        return events.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<EventResponse> searchByTitle(String title) {

        List<Event> events = repository.findByTitleContainingIgnoreCase(title);

        if (events.isEmpty()) {
            throw new ResourceNotFoundException("No events found with that title");
        }

        return events.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public EventReportResponse getEventReport(UUID eventId) {

        Event event = repository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        UUID userId = currentUserProvider.getCurrentUserId();
        String role = currentUserProvider.getCurrentUserRole();

        if (!event.getOrganizerId().equals(userId) && !role.equals("ADMIN")) {
            throw new BadRequestException("No tienes permiso para ver este reporte");
        }

        List<Seat> seats = seatRepository.findByEventId(eventId);

        SeatReportDTO vipReport = new SeatReportDTO(0,0,0,0,0);
        SeatReportDTO generalReport = new SeatReportDTO(0,0,0,0,0);

        for (Seat seat : seats) {

            SeatReportDTO report;

            if (seat.getSeatType().name().equals("VIP")) {
                report = vipReport;
            } else {
                report = generalReport;
            }

            report.setTotal(report.getTotal() + 1);

            switch (seat.getStatus()) {
                case AVAILABLE -> report.setAvailable(report.getAvailable() + 1);
                case RESERVED -> report.setReserved(report.getReserved() + 1);
                case SOLD -> {
                    report.setSold(report.getSold() + 1);
                    report.setRevenue(report.getRevenue() + seat.getPrice());
                }
            }
        }

        return EventReportResponse.builder()
                .eventId(eventId)
                .VIP(vipReport)
                .GENERAL(generalReport)
                .build();
    }
}
