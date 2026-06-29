package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.mappers.ReservationMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateReservationRequest;
import com.apicultores.backendapicultores.domain.dto.response.ReservationResponse;
import com.apicultores.backendapicultores.domain.entity.*;
import com.apicultores.backendapicultores.exception.custom.*;
import com.apicultores.backendapicultores.domain.dto.request.UpdateReservationRequest;
import com.apicultores.backendapicultores.repository.*;
import com.apicultores.backendapicultores.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final SeatRepository seatRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationStatusHistoryRepository reservationStatusHistoryRepository;
    private final EventRepository eventRepository;
    private final Clock clock;

    @Override
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request){
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found"));

        List<Seat> seats = seatRepository.findAllById(
                request.getSeatsIds()
        );

        for (Seat seat : seats) {

            if (!seat.getEvent().getEventId().equals(event.getEventId())) {

                throw new IllegalArgumentException(
                        "Seat does not belong to the event");
            }

        }

        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatUnavailableException(
                        "Seat " + seat.getSeatNumber() + " is not available");
            }
        }

        // Marcar como reservados
        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.RESERVED);
        }

        seatRepository.saveAll(seats);

        UUID userId = currentUserProvider.getCurrentUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Reservation reservation =
                reservationMapper.toEntityCreate(
                        user,
                        seats,
                        event
                );
        Reservation saved = reservationRepository.save(reservation);

        return reservationMapper.toDto(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(UUID reservationId, UpdateReservationRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("La reserva con dicho Id no se encuentra"));

        // Prevent modifying seats if reservation expired
        LocalDateTime nowSeats = LocalDateTime.now(clock);
        if (reservation.getExpiresAt() != null && reservation.getExpiresAt().isBefore(nowSeats)) {
            throw new BadRequestException("Cannot modify seats: reservation already expired");
        }
        if (request.getSeatsIds() != null && !request.getSeatsIds().isEmpty()) {
            List<Seat> seats = seatRepository.findAllById(request.getSeatsIds());
            if (!seats.isEmpty()) {
                var event = seats.get(0).getEvent();
                for (Seat s : seats) {
                    if (s.getEvent() == null || !s.getEvent().getEventId().equals(event.getEventId())) {
                        throw new BadRequestException("Todos los asientos deben pertenecer al mismo evento");
                    }
                }

                if (reservation.getEvent() != null && !event.getEventId().equals(reservation.getEvent().getEventId())) {
                    throw new BadRequestException("Los asientos deben pertenecer al mismo evento de la reserva actual");
                }
            }
            reservation.setSeats(seats);
        }

        if (request.getStatus() != null && request.getStatus() != reservation.getStatus()) {
            // Validar si no ha expirado
            LocalDateTime now = LocalDateTime.now(clock);
            if (request.getStatus() == ReservationStatus.PENDING
                    && reservation.getExpiresAt() != null
                    && reservation.getExpiresAt().isBefore(now)) {
                throw new BadRequestException("Cannot set status to PENDING: reservation already expired");
            }

            String fromStatus = reservation.getStatus() != null ? reservation.getStatus().name() : null;
            String toStatus = request.getStatus().name();

            reservation.setStatus(request.getStatus());
            Reservation updated = reservationRepository.save(reservation);

            UUID changer = currentUserProvider.getCurrentUserId();
            if (changer == null) {
                if (reservation.getUser() != null && reservation.getUser().getUserId() != null) {
                    changer = reservation.getUser().getUserId();
                } else {
                    throw new BadRequestException("No authenticated user to record status change");
                }
            }

            ReservationStatusHistory history = ReservationStatusHistory.builder()
                    .reservation(reservationRepository.getReferenceById(updated.getReservationId()))
                    .changedByUserId(changer)
                    .fromStatus(fromStatus)
                    .toStatus(toStatus)
                    .build();
            reservationStatusHistoryRepository.save(history);
        }

        Reservation saved = reservationRepository.findById(reservation.getReservationId()).orElse(reservation);
        return reservationMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("La reserva con dicho Id no se encuentra"));

        for (Seat seat : reservation.getSeats()) {
            seat.setStatus(SeatStatus.AVAILABLE);
        }

        seatRepository.saveAll(reservation.getSeats());
        reservationRepository.delete(reservation);
    }
}
