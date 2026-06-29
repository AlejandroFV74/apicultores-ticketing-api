package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.enums.EventStatus;
import com.apicultores.backendapicultores.common.enums.Role;
import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.WaitlistStatus;
import com.apicultores.backendapicultores.common.enums.SeatType;
import com.apicultores.backendapicultores.common.mappers.EventMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateEventRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateEventRequest;
import com.apicultores.backendapicultores.domain.dto.response.EventReportResponse;
import com.apicultores.backendapicultores.domain.dto.response.EventResponse;
import com.apicultores.backendapicultores.domain.dto.response.EventStatsResponse;
import com.apicultores.backendapicultores.domain.dto.response.SeatReportDTO;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.domain.entity.User;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.repository.EventRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.repository.WaitlistRepository;
import com.apicultores.backendapicultores.repository.UserRepository;
import com.apicultores.backendapicultores.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final int REQUIRED_TOTAL_SEATS = 560;
    private static final int DEFAULT_MAX_TICKETS_PER_USER = 5;

    private final EventRepository repository;
    private final EventMapper mapper;
    private final CurrentUserProvider currentUserProvider;
    private final SeatRepository seatRepository;
    private final WaitlistRepository waitlistRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        validateCreateRequest(request);

        User organizer = getOrganizer(request.getOrganizerId());

        Event event = mapper.toEntity(request);
        event.setOrganizerId(organizer.getUserId());
        event.setStatus(EventStatus.DRAFT);
        if (event.getMaxTicketsPerUser() == null) {
            event.setMaxTicketsPerUser(DEFAULT_MAX_TICKETS_PER_USER);
        }

        Event savedEvent = repository.save(event);
        seatRepository.saveAll(buildSeats(savedEvent, request));

        return mapper.toDto(savedEvent);
    }

    @Override
    public List<EventResponse> getAllEvents() {
        return repository.findByStatusAndStartDateAfterOrderByStartDateAsc(
                        EventStatus.ACTIVE,
                        LocalDateTime.now()
                )
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public EventResponse getEventById(UUID id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!isPubliclyVisible(event) && !isCurrentOrganizer(event) && !isCurrentAdmin()) {
            throw new ResourceNotFoundException("Event not found");
        }

        return mapper.toDto(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(UUID id, UpdateEventRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        validateUpdateRequest(event, request);

        if (request.getOrganizerId() != null) {
            event.setOrganizerId(getOrganizer(request.getOrganizerId()).getUserId());
        }

        return mapper.toDto(repository.save(mapper.toEntityUpdate(request, event)));
    }

    @Override
    @Transactional
    public EventResponse deleteEvent(UUID id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (event.getStatus() == EventStatus.FINISHED) {
            throw new BadRequestException("No se puede cancelar un evento finalizado");
        }

        event.setStatus(EventStatus.CANCELLED);
        return mapper.toDto(repository.save(event));
    }

    @Override
    public List<EventResponse> getMyEvents() {
        UUID userId = currentUserProvider.getCurrentUserId();

        return repository.findByOrganizerIdOrderByStartDateAsc(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<EventResponse> getManageEvents() {
        return repository.findAllByOrderByStartDateAsc()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<EventResponse> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return getAllEvents();
        }

        return repository.findByStatusAndStartDateAfterAndTitleContainingIgnoreCaseOrderByStartDateAsc(
                        EventStatus.ACTIVE,
                        LocalDateTime.now(),
                        title.trim()
                )
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    private void validateCreateRequest(CreateEventRequest request) {
        validateEventDates(request.getStartDate(), request.getEndDate(), true);

        if (request.getOrganizerId() == null) {
            throw new BadRequestException("El organizador es obligatorio");
        }

        if (request.getSeatingConfig() == null) {
            throw new BadRequestException("La configuración de asientos es obligatoria");
        }

        int totalSeats = request.getSeatingConfig().getVipSeats() +
                request.getSeatingConfig().getGeneralSeats();

        if (totalSeats != REQUIRED_TOTAL_SEATS) {
            throw new BadRequestException("El total de asientos debe ser exactamente 560. Recibido: " + totalSeats);
        }

        if (request.getSeatingConfig().getVipSeats() < 0 || request.getSeatingConfig().getGeneralSeats() < 0) {
            throw new BadRequestException("La cantidad de asientos no puede ser negativa");
        }

        if (request.getSeatingConfig().getVipPrice() < 0 || request.getSeatingConfig().getGeneralPrice() < 0) {
            throw new BadRequestException("El precio no puede ser negativo");
        }

        if (request.getMaxTicketsPerUser() != null && request.getMaxTicketsPerUser() < 1) {
            throw new BadRequestException("El máximo de tickets por usuario debe ser mayor a cero");
        }
    }

    private void validateUpdateRequest(Event event, UpdateEventRequest request) {
        if (request.getTitle() != null && request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("El título no puede estar vacío");
        }

        if (request.getVenue() != null && request.getVenue().trim().isEmpty()) {
            throw new BadRequestException("El lugar no puede estar vacío");
        }

        if (request.getMaxTicketsPerUser() != null && request.getMaxTicketsPerUser() < 1) {
            throw new BadRequestException("El máximo de tickets por usuario debe ser mayor a cero");
        }

        boolean lockedByReservationsOrSales = seatRepository.existsByEventEventIdAndStatusNot(
                event.getEventId(),
                SeatStatus.AVAILABLE
        );

        if (lockedByReservationsOrSales
                && (request.getStartDate() != null
                || request.getEndDate() != null
                || request.getMaxTicketsPerUser() != null
                || request.getOrganizerId() != null)) {
            throw new BadRequestException("No se pueden modificar fechas, organizador ni límite de tickets porque ya existen reservas o ventas");
        }

        LocalDateTime startDate = request.getStartDate() != null ? request.getStartDate() : event.getStartDate();
        LocalDateTime endDate = request.getEndDate() != null ? request.getEndDate() : event.getEndDate();

        if (request.getStartDate() != null || request.getEndDate() != null || request.getStatus() == EventStatus.ACTIVE) {
            validateEventDates(startDate, endDate, request.getStatus() == EventStatus.ACTIVE || request.getStartDate() != null);
        }
    }

    private void validateEventDates(LocalDateTime startDate, LocalDateTime endDate, boolean requireFutureStart) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas");
        }

        if (requireFutureStart && startDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        if (!startDate.isBefore(endDate)) {
            throw new BadRequestException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }

    private List<Seat> buildSeats(Event event, CreateEventRequest request) {
        List<Seat> seats = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();

        for (int i = 1; i <= request.getSeatingConfig().getVipSeats(); i++) {
            seats.add(Seat.builder()
                    .event(event)
                    .seatNumber("VIP-" + i)
                    .seatType(SeatType.VIP)
                    .price(request.getSeatingConfig().getVipPrice())
                    .status(SeatStatus.AVAILABLE)
                    .createdAt(createdAt)
                    .build());
        }

        for (int i = 1; i <= request.getSeatingConfig().getGeneralSeats(); i++) {
            seats.add(Seat.builder()
                    .event(event)
                    .seatNumber("GENERAL-" + i)
                    .seatType(SeatType.GENERAL)
                    .price(request.getSeatingConfig().getGeneralPrice())
                    .status(SeatStatus.AVAILABLE)
                    .createdAt(createdAt)
                    .build());
        }

        return seats;
    }

    private User getOrganizer(UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new BadRequestException("El organizador no existe"));

        if (organizer.getRole() != Role.ORGANIZER) {
            throw new BadRequestException("El usuario asignado debe tener rol ORGANIZER");
        }

        return organizer;
    }

    private boolean isPubliclyVisible(Event event) {
        return event.getStatus() == EventStatus.ACTIVE
                && event.getStartDate() != null
                && !event.getStartDate().isBefore(LocalDateTime.now());
    }

    @Override
    public EventResponse updateEvent(UUID id, UpdateEventRequest request) {

        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        UUID userId = currentUserProvider.getCurrentUserId();
        boolean isAdmin = currentUserProvider.isCurrentUserAdmin();
        if (!isAdmin && !event.getOrganizerId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para modificar este evento");
        }

        return mapper.toDto(repository.save(mapper.toEntityUpdate(request, event)));
    }

    @Override
    public void deleteEvent(UUID id) {

        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        UUID userId = currentUserProvider.getCurrentUserId();
        boolean isAdmin = currentUserProvider.isCurrentUserAdmin();

        if (!isAdmin && !event.getOrganizerId().equals(userId)) {
            throw new BadRequestException("No tienes permiso para eliminar este evento");
        }

        repository.delete(event);
    private boolean isCurrentOrganizer(Event event) {
        UUID userId = getAuthenticatedUserIdOrNull();
        return userId != null && event.getOrganizerId().equals(userId);
    }

    private boolean isCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private UUID getAuthenticatedUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getUserId();
        }

        return null;
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
