package com.apicultores.backendapicultores.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservation")
public class Reservation {
    //Temporal
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

}
