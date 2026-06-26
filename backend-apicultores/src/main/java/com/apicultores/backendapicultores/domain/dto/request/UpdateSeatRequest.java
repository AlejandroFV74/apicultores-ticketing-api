package com.apicultores.backendapicultores.domain.dto.request;

import com.apicultores.backendapicultores.common.enums.SeatStatus;
import com.apicultores.backendapicultores.common.enums.SeatType;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeatRequest {

    private String seatNumber;

    private SeatType seatType;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    private SeatStatus status;
}
