package com.apicultores.backendapicultores.domain.dto.request;

import com.apicultores.backendapicultores.common.enums.EventStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateEventRequest {
    @NotNull(message = "El organizador es obligatorio")
    private UUID organizerId;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String venue;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private EventStatus status;

    private Integer maxTicketsPerUser;

    @Valid
    @NotNull(message = "La configuración de asientos es requerida")
    private SeatingConfigRequest seatingConfig;
}
