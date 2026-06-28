package com.apicultores.backendapicultores.services.waitlist;
import com.apicultores.backendapicultores.common.enums.*;
import com.apicultores.backendapicultores.common.mappers.WaitlistMapper;
import com.apicultores.backendapicultores.domain.dto.request.JoinWaitlistRequest;
import com.apicultores.backendapicultores.domain.dto.response.waitlist.WaitlistResponse;
import com.apicultores.backendapicultores.domain.entity.*;
import com.apicultores.backendapicultores.exception.custom.*;
import com.apicultores.backendapicultores.repository.*;
import com.apicultores.backendapicultores.services.notification.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final WaitlistMapper waitlistMapper;
    private final NotificationService notificationService;

    @Transactional
    public WaitlistResponse join(User user, JoinWaitlistRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        long availableSeats = seatRepository.countByEvent_EventIdAndSeatTypeAndStatus(
                event.getEventId(), request.getSeatType(), SeatStatus.AVAILABLE);

        if (availableSeats > 0) {
            throw new BadRequestException(
                    "Todavía hay asientos disponibles de tipo " + request.getSeatType() +
                            " para este evento, no es necesario unirse a la lista de espera");
        }

        boolean alreadyWaiting = waitlistRepository.existsByUser_UserIdAndEvent_EventIdAndSeatTypeAndStatus(
                user.getUserId(), event.getEventId(), request.getSeatType(), WaitlistStatus.WAITING);

        if (alreadyWaiting) {
            throw new AlreadyInWaitlistException("Ya estás en la lista de espera para esta localidad");
        }

        Waitlist waitlist = Waitlist.builder()
                .user(user)
                .event(event)
                .seatType(request.getSeatType())
                .status(WaitlistStatus.WAITING)
                .build();

        Waitlist saved = waitlistRepository.save(waitlist);
        int position = getPosition(saved);
        return waitlistMapper.toDto(saved, position);
    }

    @Transactional
    public void leave(UUID waitlistId, UUID userId) {
        Waitlist waitlist = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new WaitlistNotFoundException("Registro de lista de espera no encontrado"));

        if (!waitlist.getUser().getUserId().equals(userId)) {
            throw new WaitlistNotFoundException("Registro de lista de espera no encontrado");
        }

        if (waitlist.getStatus() == WaitlistStatus.RESERVED) {
            throw new ReservationStatusException(
                    "Ya tienes una reserva activa generada desde la lista de espera, cancélala desde tus reservas");
        }

        waitlist.setStatus(WaitlistStatus.CANCELLED);
        waitlistRepository.save(waitlist);
    }

    public List<WaitlistResponse> getMyWaitlist(UUID userId) {
        return waitlistRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(w -> waitlistMapper.toDto(w, w.getStatus() == WaitlistStatus.WAITING ? getPosition(w) : null))
                .collect(Collectors.toList());
    }

    public List<WaitlistResponse> getEventWaitlist(UUID eventId) {
        return waitlistRepository.findByEvent_EventIdOrderByCreatedAtAsc(eventId).stream()
                .map(w -> waitlistMapper.toDto(w, getPosition(w)))
                .collect(Collectors.toList());
    }

    private int getPosition(Waitlist target) {
        List<Waitlist> waiting = waitlistRepository.findByEvent_EventIdOrderByCreatedAtAsc(
                target.getEvent().getEventId());
        int position = 1;
        for (Waitlist w : waiting) {
            if (w.getStatus() != WaitlistStatus.WAITING) continue;
            if (w.getWaitlistId().equals(target.getWaitlistId())) return position;
            position++;
        }
        return position;
    }

    @Transactional
    public void processFreedSeat(UUID eventId, SeatType seatType) {
        List<Waitlist> candidates = waitlistRepository.findByEvent_EventIdAndSeatTypeAndStatusOrderByCreatedAtAsc(
                eventId, seatType, WaitlistStatus.WAITING, PageRequest.of(0, 1));

        if (candidates.isEmpty()) {
            return;
        }

        List<Seat> availableSeats = seatRepository.findAvailableForUpdate(
                eventId, seatType, SeatStatus.AVAILABLE, PageRequest.of(0, 1));

        if (availableSeats.isEmpty()) {
            return;
        }

        Waitlist waitlist = candidates.get(0);
        Seat seat = availableSeats.get(0);

        seat.setStatus(SeatStatus.RESERVED);
        seatRepository.save(seat);

        Reservation reservation = Reservation.builder()
                .user(waitlist.getUser())
                .event(seat.getEvent())
                .status(ReservationStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        reservation.getSeats().add(seat);
        Reservation savedReservation = reservationRepository.save(reservation);

        waitlist.setStatus(WaitlistStatus.RESERVED);
        waitlist.setNotifiedAt(LocalDateTime.now());
        waitlist.setReservation(savedReservation);
        waitlistRepository.save(waitlist);

        notificationService.createAndPush(
                waitlist.getUser(),
                NotificationType.SEAT_AVAILABLE,
                "¡Se liberó un asiento para " + seat.getEvent().getTitle() + "!",
                "Te reservamos el asiento " + seat.getSeatNumber() + " (" + seatType +
                        "). Tienes 15 minutos para completar el pago antes de perder el cupo.",
                seat.getEvent().getEventId(),
                savedReservation.getReservationId()
        );

        log.info("Waitlist: asiento {} del evento {} asignado automáticamente al usuario {}",
                seat.getSeatNumber(), eventId, waitlist.getUser().getUserId());
    }
}