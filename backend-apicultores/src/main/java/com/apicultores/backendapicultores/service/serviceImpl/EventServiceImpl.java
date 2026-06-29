package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.WaitlistStatus;
import com.apicultores.backendapicultores.common.mappers.EventMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateEventRequest;
import com.apicultores.backendapicultores.domain.dto.request.SeatConfigurationRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateEventRequest;
import com.apicultores.backendapicultores.domain.dto.response.EventReportResponse;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;
import com.apicultores.backendapicultores.domain.dto.response.EventStatsResponse;
import com.apicultores.backendapicultores.domain.dto.response.SeatReportDTO;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.repository.EventRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.repository.WaitlistRepository;
import com.apicultores.backendapicultores.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventMapper mapper;
    private final CurrentUserProvider currentUserProvider;
    private final SeatRepository seatRepository;
    private final WaitlistRepository waitlistRepository;

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

        if (request.getSeatingConfig() == null) {
            throw new BadRequestException("La configuración de asientos es obligatoria");
        }

        int totalSeats = request.getSeatingConfig().getVipSeats() + 
                        request.getSeatingConfig().getGeneralSeats();
        
        if (totalSeats != 560) {
            throw new BadRequestException("El total de asientos debe ser exactamente 560. Recibido: " + totalSeats);
        }

        if (request.getSeatingConfig().getVipSeats() < 0 || request.getSeatingConfig().getGeneralSeats() < 0) {
            throw new BadRequestException("La cantidad de asientos no puede ser negativa");
        }

        if (request.getSeatingConfig().getVipPrice() < 0 || request.getSeatingConfig().getGeneralPrice() < 0) {
            throw new BadRequestException("El precio no puede ser negativo");
        }

        UUID userId = currentUserProvider.getCurrentUserId();

        Event event = mapper.toEntity(request);
        event.setOrganizerId(userId);

        Event savedEvent = repository.save(event);

        List<Seat> seats = new ArrayList<>();

        // Create VIP seats
        for (int i = 1; i <= request.getSeatingConfig().getVipSeats(); i++) {
            Seat seat = Seat.builder()
                    .event(savedEvent)
                    .seatNumber("VIP-" + i)
                    .seatType(com.apicultores.backendapicultores.common.enums.SeatType.VIP)
                    .price(request.getSeatingConfig().getVipPrice())
                    .status(SeatStatus.AVAILABLE)
                    .createdAt(LocalDateTime.now())
                    .build();
            seats.add(seat);
        }

        // Create GENERAL seats
        for (int i = 1; i <= request.getSeatingConfig().getGeneralSeats(); i++) {
            Seat seat = Seat.builder()
                    .event(savedEvent)
                    .seatNumber("GENERAL-" + i)
                    .seatType(com.apicultores.backendapicultores.common.enums.SeatType.GENERAL)
                    .price(request.getSeatingConfig().getGeneralPrice())
                    .status(SeatStatus.AVAILABLE)
                    .createdAt(LocalDateTime.now())
                    .build();
            seats.add(seat);
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

        List<Seat> seats = seatRepository.findByEventEventId(eventId);

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

    @Override
    public EventStatsResponse getEventStats(UUID id) {

        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        UUID userId = currentUserProvider.getCurrentUserId();
        boolean isAdmin = currentUserProvider.isCurrentUserAdmin();

        if (!event.getOrganizerId().equals(userId) && !isAdmin) {
            throw new BadRequestException("No tienes permiso para ver las estadísticas de este evento");
        }
        List<Seat> seats = seatRepository.findByEvent_EventId(id);

        if (seats == null || seats.isEmpty()) {
            return EventStatsResponse.builder()
                    .eventId(event.getEventId())
                    .title(event.getTitle())
                    .seatsTotal(0)
                    .byType(new ArrayList<>())
                    .build();
        }

        long total = seats.size();
        long available = 0;
        long reserved = 0;
        long sold = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        Map<String, EventStatsResponse.SeatTypeStat> statsByType = new HashMap<>();

        for (Seat seat : seats) {
            String seatTypeName = seat.getSeatType().name();

            EventStatsResponse.SeatTypeStat typeStat = statsByType.computeIfAbsent(seatTypeName,
                    k -> EventStatsResponse.SeatTypeStat.builder()
                            .seatType(seatTypeName)
                            .revenue(BigDecimal.ZERO)
                            .build());

            typeStat.setTotal(typeStat.getTotal() + 1);
            SeatStatus status = seat.getStatus();

            if (status == SeatStatus.AVAILABLE) {
                available++;
                typeStat.setAvailable(typeStat.getAvailable() + 1);

            } else if (status == SeatStatus.RESERVED) {
                reserved++;
                typeStat.setReserved(typeStat.getReserved() + 1);

            } else if (status == SeatStatus.SOLD) {
                sold++;
                typeStat.setSold(typeStat.getSold() + 1);

                BigDecimal seatPrice = BigDecimal.valueOf(seat.getPrice());
                totalRevenue = totalRevenue.add(seatPrice);
                typeStat.setRevenue(typeStat.getRevenue().add(seatPrice));
            }
        }

        double occupancyRate = (double) sold / total * 100.0;

        long waitlistCount = waitlistRepository.countByEvent_EventIdAndStatus(id, WaitlistStatus.WAITING);

        return EventStatsResponse.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .seatsTotal(total)
                .seatsAvailable(available)
                .seatsReserved(reserved)
                .seatsSold(sold)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .revenue(totalRevenue)
                .waitlistCount(waitlistCount)
                .byType(new ArrayList<>(statsByType.values()))
                .build();
    }
}
