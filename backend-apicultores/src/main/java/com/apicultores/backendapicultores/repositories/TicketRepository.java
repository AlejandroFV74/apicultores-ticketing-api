package com.apicultores.backendapicultores.repositories;

import com.apicultores.backendapicultores.domain.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByQrCode(String qrCode);
}
