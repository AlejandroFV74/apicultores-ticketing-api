package com.apicultores.backendapicultores.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeatingConfigRequest {

    @NotNull(message = "La cantidad de asientos VIP es requerida")
    @Min(value = 0, message = "La cantidad de asientos VIP no puede ser negativa")
    private Integer vipSeats;

    @NotNull(message = "El precio de asientos VIP es requerido")
    @Min(value = 0, message = "El precio de asientos VIP no puede ser negativo")
    private Double vipPrice;

    @NotNull(message = "La cantidad de asientos generales es requerida")
    @Min(value = 0, message = "La cantidad de asientos generales no puede ser negativa")
    private Integer generalSeats;

    @NotNull(message = "El precio de asientos generales es requerido")
    @Min(value = 0, message = "El precio de asientos generales no puede ser negativo")
    private Double generalPrice;
}
