package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.domain.entity.TicketTransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketTransferHistoryRepository extends JpaRepository<TicketTransferHistory, UUID> {
}
