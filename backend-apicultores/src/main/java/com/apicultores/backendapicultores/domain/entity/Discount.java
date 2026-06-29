package com.apicultores.backendapicultores.domain.entity;

import com.apicultores.backendapicultores.common.enums.DiscountCategory;
import com.apicultores.backendapicultores.common.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discount")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "discount_id")
    private UUID discountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private DiscountCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "min_tickets")
    @Builder.Default
    private Integer minTickets = 1;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean isAutomatic() {
        return category != DiscountCategory.CODE;
    }
}