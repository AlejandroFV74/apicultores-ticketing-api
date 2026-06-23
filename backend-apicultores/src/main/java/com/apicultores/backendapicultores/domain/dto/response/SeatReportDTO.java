package com.apicultores.backendapicultores.domain.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatReportDTO {

    private int total;
    private int available;
    private int reserved;
    private int sold;
    private double revenue;
}
