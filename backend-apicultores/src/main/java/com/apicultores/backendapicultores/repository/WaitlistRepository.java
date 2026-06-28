package com.apicultores.backendapicultores.repository;
import com.apicultores.backendapicultores.common.enums.SeatType;
import com.apicultores.backendapicultores.common.enums.WaitlistStatus;
import com.apicultores.backendapicultores.domain.entity.Waitlist;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, UUID> {

    boolean existsByUser_UserIdAndEvent_EventIdAndSeatTypeAndStatus(
            UUID userId, UUID eventId, SeatType seatType, WaitlistStatus status);

    List<Waitlist> findByUser_UserIdOrderByCreatedAtDesc(UUID userId);

    List<Waitlist> findByEvent_EventIdOrderByCreatedAtAsc(UUID eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Waitlist> findByEvent_EventIdAndSeatTypeAndStatusOrderByCreatedAtAsc(
            UUID eventId, SeatType seatType, WaitlistStatus status, Pageable pageable);

    Optional<Waitlist> findByReservation_ReservationId(UUID reservationId);
}