package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.SeatType;
import com.apicultores.backendapicultores.domain.entity.Seat;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    @EntityGraph(attributePaths = {"event","tickets"})
    List<Seat> findAllById(Iterable<UUID> ids);
    long countByEvent_EventIdAndSeatTypeAndStatus(UUID eventId, @NotNull SeatType seatType, SeatStatus seatStatus);

    @Query("SELECT s FROM Seat s " +
            "WHERE s.event.eventId = :eventId " +
            "AND s.seatType = :seatType " +
            "AND s.status = :status " +
            "ORDER BY s.seatNumber ASC")
    List<Seat> findAvailableForUpdate(@Param("eventId") UUID eventId,
                                      @Param("seatType") SeatType seatType,
                                      @Param("status") SeatStatus status,
                                      Pageable pageable);
  
    @EntityGraph(attributePaths = {"event"})
    List<Seat> findByEventEventId(UUID eventId);
    List<Seat> findByEvent_EventId(UUID id);
    boolean existsByEventEventIdAndStatusNot(UUID eventId, SeatStatus status);
}
