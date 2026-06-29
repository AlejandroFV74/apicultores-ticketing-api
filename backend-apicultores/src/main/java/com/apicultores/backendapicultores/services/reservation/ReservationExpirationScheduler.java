package com.apicultores.backendapicultores.services.reservation;
import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.repository.ReservationRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import com.apicultores.backendapicultores.services.waitlist.WaitlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationExpirationScheduler {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final WaitlistService waitlistService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireOverdueReservations() {
        List<Reservation> expired = reservationRepository.findExpired(
                ReservationStatus.PENDING, LocalDateTime.now());

        if (expired.isEmpty()) {
            return;
        }

        Set<String> processedKeys = new HashSet<>();

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);

            for (Seat seat : reservation.getSeats()) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }

            log.info("Reserva {} expirada, {} asiento(s) liberado(s)",
                    reservation.getReservationId(), reservation.getSeats().size());
        }

        for (Reservation reservation : expired) {
            for (Seat seat : reservation.getSeats()) {
                String key = seat.getEvent().getEventId() + ":" + seat.getSeatType();
                if (processedKeys.add(key)) {
                    waitlistService.processFreedSeat(seat.getEvent().getEventId(), seat.getSeatType());
                }
            }
        }
    }
}