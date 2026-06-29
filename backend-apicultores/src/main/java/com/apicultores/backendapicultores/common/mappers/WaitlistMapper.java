package com.apicultores.backendapicultores.common.mappers;

import com.apicultores.backendapicultores.domain.dto.response.waitlist.WaitlistResponse;
import com.apicultores.backendapicultores.domain.entity.Waitlist;
import org.springframework.stereotype.Component;

@Component
public class WaitlistMapper {
    public WaitlistResponse toDto(Waitlist w, Integer position) {
        return WaitlistResponse.builder()
                .waitlistId(w.getWaitlistId())
                .eventId(w.getEvent().getEventId())
                .eventName(w.getEvent().getTitle())
                .seatType(w.getSeatType())
                .status(w.getStatus())
                .position(position)
                .notifiedAt(w.getNotifiedAt())
                .reservationId(w.getReservation() != null ? w.getReservation().getReservationId() : null)
                .createdAt(w.getCreatedAt())
                .build();
    }
}