package com.apicultores.backendapicultores.repository;

import com.apicultores.backendapicultores.common.enums.DiscountCategory;
import com.apicultores.backendapicultores.domain.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, UUID> {

    List<Discount> findByEvent_EventId(UUID eventId);

    List<Discount> findByEvent_EventIdAndCategoryIn(UUID eventId, List<DiscountCategory> categories);

    Optional<Discount> findByEvent_EventIdAndCodeIgnoreCase(UUID eventId, String code);
}
