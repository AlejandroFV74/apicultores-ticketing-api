package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReservationReservationId(UUID reservationId);
}
