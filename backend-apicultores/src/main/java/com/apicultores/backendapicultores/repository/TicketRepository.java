package com.apicultores.backendapicultores.repository;

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
  
    @Query("SELECT t FROM Ticket t " +
            "JOIN FETCH t.seat s " +
            "JOIN FETCH s.event " +
            "JOIN FETCH t.owner o " +
            "WHERE o.id = :ownerId")
    Optional<List<Ticket>> findByOwner_UserId(@Param("ownerId") UUID owner_id);
    @Query("SELECT t FROM Ticket t " +
            "JOIN FETCH t.seat s " +
            "JOIN FETCH s.event " +
            "JOIN FETCH t.owner")
    Optional<List<Ticket>> findAllWithSeat();

    @Query("SELECT COUNT(t) FROM Ticket t " +
            "WHERE t.owner.id = :userId " +
            "AND t.seat.event.eventId = :eventId " +
            "AND t.status IN ('PAID', 'USED')")
    long countTicketByUserAndEvent(@Param("userId") UUID userId, @Param("eventId") UUID eventId);

    @Query("SELECT t FROM Ticket t " +
            "JOIN FETCH t.seat s " +
            "JOIN FETCH s.event " +
            "JOIN FETCH t.owner o " +
            "WHERE o.id = :ownerId AND t.status = USED")
    Optional<List<Ticket>> findByStatusUsedAndOwner(@Param("ownerId") UUID owner_id);


    @Query("SELECT t FROM Ticket t " +
            "JOIN FETCH t.seat s " +
            "JOIN FETCH s.event " +
            "JOIN FETCH t.owner o " +
            "WHERE o.id = :ownerId AND t.status = PAID")
    Optional<List<Ticket>> findActiveTicketsByOwner(@Param("ownerId") UUID owner_id);
    Optional<List<Ticket>> findByOwner(UUID id);

    @Query("SELECT DISTINCT t FROM Ticket t " +
            "JOIN FETCH t.seat s " +
            "JOIN FETCH s.event " +
            "JOIN FETCH t.owner")
    List<Ticket> findAllWithEagerLoad();

    @Query("SELECT DISTINCT t FROM Ticket t " +
            "JOIN FETCH t.seat s " +
            "JOIN FETCH s.event " +
            "JOIN FETCH t.owner " +
            "WHERE t.owner.userId = :ownerId")
    Optional<List<Ticket>> findByOwnerWithEagerLoad(@Param("ownerId") UUID ownerId);
}
