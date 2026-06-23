package com.apicultores.backendapicultores.service.serviceImpl;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import com.apicultores.backendapicultores.domain.entity.ReservationStatusHistory;
import com.apicultores.backendapicultores.repository.ReservationRepository;
import com.apicultores.backendapicultores.repository.ReservationStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationExpiryService {

    private final ReservationRepository reservationRepository;
    private final ReservationStatusHistoryRepository historyRepository;
    private final Clock clock;

    @Transactional
    public List<Reservation> expireReservations() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Reservation> toExpire = reservationRepository.findByExpiresAtBeforeAndStatusNotIn(now, List.of(ReservationStatus.COMPLETED, ReservationStatus.EXPIRED));

        for (Reservation r : toExpire) {
            String from = r.getStatus() != null ? r.getStatus().name() : null;
            r.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(r);

            ReservationStatusHistory hist = ReservationStatusHistory.builder()
                    .reservation(r)
                    .changedByUserId(null)
                    .fromStatus(from)
                    .toStatus(ReservationStatus.EXPIRED.name())
                    .build();
            historyRepository.save(hist);
        }

        return toExpire;
    }
}
