package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.SeatType;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.request.UpdateSeatRequest;
import com.apicultores.backendapicultores.domain.dto.response.SeatResponse;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.common.mappers.SeatMapper;
import com.apicultores.backendapicultores.repository.EventRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final SeatMapper seatMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public SeatResponse createSeat(CreateSeatRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateCanManageEvent(event);

        Seat seat = seatMapper.toEntity(request, event);
        Seat savedSeat = seatRepository.save(seat);

        return seatMapper.toDto(savedSeat);
    }

    @Override
    public List<SeatResponse> getAllSeats() {
        List<Seat> seats = seatRepository.findAll();
        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("No seats found");
        }
        return seats.stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    public SeatResponse getSeatById(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        return seatMapper.toDto(seat);
    }

    @Override
    public List<SeatResponse> getSeatsByEventId(UUID eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        List<Seat> seats = seatRepository.findByEventEventId(eventId);

        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("No seats found for this event");
        }

        return seats.stream()
                .sorted(SEAT_LAYOUT_COMPARATOR)
                .map(seatMapper::toDto)
                .toList();
    }

    private static final Comparator<Seat> SEAT_LAYOUT_COMPARATOR = Comparator
            .comparingInt(SeatServiceImpl::seatTypeOrder)
            .thenComparingInt(SeatServiceImpl::seatNumberOrder)
            .thenComparing(Seat::getSeatNumber, Comparator.nullsLast(String::compareToIgnoreCase));

    private static int seatTypeOrder(Seat seat) {
        SeatType seatType = seat.getSeatType();
        if (seatType == SeatType.VIP) {
            return 0;
        }
        if (seatType == SeatType.GENERAL) {
            return 1;
        }
        if (seatType == SeatType.PREFERENCIAL) {
            return 2;
        }
        return 3;
    }

    private static int seatNumberOrder(Seat seat) {
        String seatNumber = seat.getSeatNumber();
        if (seatNumber == null || seatNumber.isBlank()) {
            return Integer.MAX_VALUE;
        }

        int end = seatNumber.length() - 1;
        while (end >= 0 && !Character.isDigit(seatNumber.charAt(end))) {
            end--;
        }

        if (end < 0) {
            return Integer.MAX_VALUE;
        }

        int start = end;
        while (start >= 0 && Character.isDigit(seatNumber.charAt(start))) {
            start--;
        }

        try {
            return Integer.parseInt(seatNumber.substring(start + 1, end + 1));
        } catch (NumberFormatException ex) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    @Transactional
    public SeatResponse updateSeat(UUID seatId, UpdateSeatRequest request) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        validateCanManageEvent(seat.getEvent());
        validateSeatCanBeConfigured(seat, request);

        Seat updatedSeat = seatMapper.toEntityUpdate(request, seat);
        Seat savedSeat = seatRepository.save(updatedSeat);

        return seatMapper.toDto(savedSeat);
    }

    @Override
    @Transactional
    public void deleteSeat(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        validateCanManageEvent(seat.getEvent());
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new BadRequestException("No se puede eliminar un asiento reservado o vendido");
        }
        seatRepository.delete(seat);
    }
    private void validateCanManageEvent(Event event) {
        if (isCurrentAdmin()) {
            return;
        }

        UUID currentUserId = currentUserProvider.getCurrentUserId();
        if (!event.getOrganizerId().equals(currentUserId)) {
            throw new BadRequestException("Solo el organizador asignado puede configurar localidades de este evento");
        }
    }

    private void validateSeatCanBeConfigured(Seat seat, UpdateSeatRequest request) {
        if (request.getStatus() != null) {
            throw new BadRequestException("El estado del asiento se actualiza desde reservas, pagos o reembolsos");
        }

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new BadRequestException("No se puede modificar un asiento reservado o vendido");
        }
    }

    private boolean isCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
