package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReservationReservationId(UUID reservationId);

    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.reservation r
        JOIN FETCH r.user
        WHERE p.paymentId = :paymentId
    """)
    Optional<Payment> findByIdWithReservationAndUser(@Param("paymentId") UUID paymentId);
}
