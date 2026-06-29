package com.apicultores.backendapicultores.services.discount;
import com.apicultores.backendapicultores.common.enums.DiscountCategory;
import com.apicultores.backendapicultores.common.enums.DiscountType;
import com.apicultores.backendapicultores.common.mappers.DiscountMapper;
import com.apicultores.backendapicultores.config.security.CurrentUserProvider;
import com.apicultores.backendapicultores.domain.dto.request.CreateDiscountRequest;
import com.apicultores.backendapicultores.domain.dto.request.PriceQuoteRequest;
import com.apicultores.backendapicultores.domain.dto.response.discount.DiscountResponse;
import com.apicultores.backendapicultores.domain.dto.response.discount.PriceQuoteResponse;
import com.apicultores.backendapicultores.domain.entity.Discount;
import com.apicultores.backendapicultores.domain.entity.Event;
import com.apicultores.backendapicultores.domain.entity.Seat;
import com.apicultores.backendapicultores.exception.custom.BadRequestException;
import com.apicultores.backendapicultores.exception.custom.InvalidDiscountException;
import com.apicultores.backendapicultores.exception.custom.ResourceNotFoundException;
import com.apicultores.backendapicultores.repository.DiscountRepository;
import com.apicultores.backendapicultores.repository.EventRepository;
import com.apicultores.backendapicultores.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final DiscountMapper discountMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DiscountResponse create(CreateDiscountRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        UUID organizerId = currentUserProvider.getCurrentUserId();
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new BadRequestException("No tienes permiso para configurar descuentos de este evento");
        }

        if (request.getCategory() == DiscountCategory.CODE &&
                (request.getCode() == null || request.getCode().isBlank())) {
            throw new InvalidDiscountException("Los descuentos por código requieren especificar un código");
        }

        if (request.getCategory() == DiscountCategory.GROUP &&
                (request.getMinTickets() == null || request.getMinTickets() < 2)) {
            throw new InvalidDiscountException("Un descuento por grupo debe exigir al menos 2 tickets");
        }

        if (request.getCategory() == DiscountCategory.EARLY_BIRD && request.getValidUntil() == null) {
            throw new InvalidDiscountException("Un descuento early-bird requiere una fecha límite (validUntil)");
        }

        if (request.getDiscountType() == DiscountType.PERCENTAGE &&
                request.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidDiscountException("Un descuento porcentual no puede superar el 100%");
        }

        Discount discount = discountMapper.toEntity(request, event);
        return discountMapper.toDto(discountRepository.save(discount));
    }

    public List<DiscountResponse> getByEvent(UUID eventId) {
        return discountRepository.findByEvent_EventId(eventId).stream()
                .map(discountMapper::toDto)
                .collect(Collectors.toList());
    }

    public PriceQuoteResponse quote(PriceQuoteRequest request) {
        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        if (seats.size() != request.getSeatIds().size()) {
            throw new ResourceNotFoundException("Uno o más asientos no existen");
        }

        BigDecimal subtotal = seats.stream()
                .map(s -> BigDecimal.valueOf(s.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int ticketCount = seats.size();
        LocalDateTime now = LocalDateTime.now();

        List<Discount> automaticCandidates = discountRepository.findByEvent_EventIdAndCategoryIn(
                request.getEventId(), List.of(DiscountCategory.EARLY_BIRD, DiscountCategory.GROUP));

        Optional<Discount> bestAutomatic = automaticCandidates.stream()
                .filter(d -> isAutomaticDiscountEligible(d, ticketCount, now))
                .max((a, b) -> computeDiscountAmount(a, subtotal).compareTo(computeDiscountAmount(b, subtotal)));

        List<PriceQuoteResponse.AppliedDiscount> applied = new ArrayList<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal remaining = subtotal;

        if (bestAutomatic.isPresent()) {
            Discount d = bestAutomatic.get();
            BigDecimal amount = computeDiscountAmount(d, subtotal);
            remaining = remaining.subtract(amount);
            totalDiscount = totalDiscount.add(amount);
            applied.add(PriceQuoteResponse.AppliedDiscount.builder()
                    .description(d.getDescription() != null ? d.getDescription() : d.getCategory().name())
                    .category(d.getCategory().name())
                    .amountDiscounted(amount)
                    .build());
        }

        if (request.getCode() != null && !request.getCode().isBlank()) {
            Discount codeDiscount = discountRepository
                    .findByEvent_EventIdAndCodeIgnoreCase(request.getEventId(), request.getCode())
                    .orElseThrow(() -> new InvalidDiscountException("El código de descuento no es válido para este evento"));

            if (codeDiscount.getValidUntil() != null && now.isAfter(codeDiscount.getValidUntil())) {
                throw new InvalidDiscountException("El código de descuento ya expiró");
            }
            if (ticketCount < codeDiscount.getMinTickets()) {
                throw new InvalidDiscountException(
                        "El código requiere al menos " + codeDiscount.getMinTickets() + " tickets");
            }

            BigDecimal amount = computeDiscountAmount(codeDiscount, subtotal);
            remaining = remaining.subtract(amount);
            totalDiscount = totalDiscount.add(amount);
            applied.add(PriceQuoteResponse.AppliedDiscount.builder()
                    .description(codeDiscount.getDescription() != null
                            ? codeDiscount.getDescription() : "Código " + codeDiscount.getCode())
                    .category(codeDiscount.getCategory().name())
                    .amountDiscounted(amount)
                    .build());
        }

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        return PriceQuoteResponse.builder()
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .appliedDiscounts(applied)
                .totalDiscount(totalDiscount.setScale(2, RoundingMode.HALF_UP))
                .total(remaining.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private boolean isAutomaticDiscountEligible(Discount d, int ticketCount, LocalDateTime now) {
        if (d.getCategory() == DiscountCategory.EARLY_BIRD) {
            return d.getValidUntil() != null && now.isBefore(d.getValidUntil());
        }
        if (d.getCategory() == DiscountCategory.GROUP) {
            return d.getMinTickets() != null && ticketCount >= d.getMinTickets();
        }
        return false;
    }

    private BigDecimal computeDiscountAmount(Discount d, BigDecimal subtotal) {
        if (d.getDiscountType() == DiscountType.PERCENTAGE) {
            return subtotal.multiply(d.getValue()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        }
        return d.getValue().min(subtotal);
    }
}