package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.SeatType;
import com.apicultores.backendapicultores.domain.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    @EntityGraph(attributePaths = {"event","tickets"})
    List<Seat> findAllById(Iterable<UUID> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findBySeatIdIn(List<UUID> seatIds);

    long countByEvent_EventIdAndSeatTypeAndStatus(UUID eventId, SeatType seatType, SeatStatus status);

    List<Seat> findByEvent_EventId(UUID eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s " +
            "WHERE s.event.eventId = :eventId " +
            "AND s.seatType = :seatType " +
            "AND s.status = :status " +
            "ORDER BY s.seatNumber ASC")
    List<Seat> findAvailableForUpdate(@Param("eventId") UUID eventId,
                                      @Param("seatType") SeatType seatType,
                                      @Param("status") SeatStatus status,
                                      Pageable pageable);
}