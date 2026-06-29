package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.common.enums.TicketStatus;
import com.apicultores.backendapicultores.domain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByQrCode(String qrCode);
    @Query("""
        SELECT DISTINCT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.event
        JOIN FETCH t.owner o
        WHERE o.userId = :ownerId
    """)
    List<Ticket> findByOwnerWithEagerLoad(@Param("ownerId") UUID ownerId);

    @Query("""
        SELECT DISTINCT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.event
        JOIN FETCH t.owner o
        WHERE o.userId = :ownerId
        AND t.status = :status
    """)
    List<Ticket> findByOwnerAndStatus(
            @Param("ownerId") UUID ownerId,
            @Param("status") TicketStatus status
    );

    @Query("""
        SELECT COUNT(t) FROM Ticket t
        WHERE t.owner.userId = :userId
        AND t.seat.event.eventId = :eventId
        AND t.status IN :statuses
    """)
    long countTicketByUserAndEvent(
            @Param("userId") UUID userId,
            @Param("eventId") UUID eventId,
            @Param("statuses") List<TicketStatus> statuses
    );

    @Query("""
        SELECT DISTINCT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.event
        JOIN FETCH t.owner
    """)
    List<Ticket> findAllWithEagerLoad();

    @Query("""
        SELECT DISTINCT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.event
        JOIN FETCH t.owner
        WHERE t.reservation.reservationId = :reservationId
    """)
    List<Ticket> findByReservationWithEagerLoad(@Param("reservationId") UUID reservationId);

    @Query("""
        SELECT DISTINCT t FROM Ticket t
        JOIN FETCH t.seat s
        JOIN FETCH s.event
        JOIN FETCH t.owner
        WHERE t.payment.paymentId = :paymentId
    """)
    List<Ticket> findByPaymentWithEagerLoad(@Param("paymentId") UUID paymentId);

    @Query("""
    SELECT DISTINCT t FROM Ticket t
    JOIN FETCH t.seat s
    JOIN FETCH s.event
    JOIN FETCH t.owner o
    WHERE o.userId = :ownerId
    AND t.status = :status
""")
    List<Ticket> findActiveTicketsByOwner(
            @Param("ownerId") UUID ownerId,
            @Param("status") TicketStatus status
    );
}
