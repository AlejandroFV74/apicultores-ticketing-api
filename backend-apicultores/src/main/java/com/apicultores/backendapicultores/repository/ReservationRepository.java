package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.common.enums.ReservationStatus;
import com.apicultores.backendapicultores.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    java.util.List<Reservation> findByExpiresAtBeforeAndStatusNotIn(LocalDateTime time, List<ReservationStatus> statuses);
}
