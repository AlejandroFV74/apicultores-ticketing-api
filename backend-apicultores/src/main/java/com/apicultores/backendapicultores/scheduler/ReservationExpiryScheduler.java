package com.apicultores.backendapicultores.scheduler;

import com.apicultores.backendapicultores.service.serviceImpl.ReservationExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryScheduler {

    private final ReservationExpiryService expiryService;

    // Corriendo siempre para prueba
    @Scheduled(fixedDelayString = "${app.reservation.expiry-check-ms:60000}")
    public void runExpiry() {
        try {
            var expired = expiryService.expireReservations();
            if (!expired.isEmpty()) {
                log.info("Expired {} reservations", expired.size());
            }
        } catch (Exception e) {
            log.error("Error expiring reservations", e);
        }
    }
}
