package com.apicultores.backendapicultores.domain.dto.request;

import com.apicultores.backendapicultores.common.enums.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SeatConfigurationRequest {

    @NotNull(message = "El tipo de asiento es requerido")
    private SeatType seatType;

    @NotNull(message = "El precio es requerido")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double price;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "Debe haber al menos un asiento por tipo de asiento")
    private Integer quantity;
}
