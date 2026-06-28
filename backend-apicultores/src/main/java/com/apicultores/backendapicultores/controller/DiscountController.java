package com.apicultores.backendapicultores.controller;
import com.apicultores.backendapicultores.domain.dto.request.CreateDiscountRequest;
import com.apicultores.backendapicultores.domain.dto.request.PriceQuoteRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.services.discount.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateDiscountRequest request) {
        return buildResponse("Descuento creado", HttpStatus.CREATED, discountService.create(request));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<GeneralResponse> byEvent(@PathVariable UUID eventId) {
        return buildResponse("Descuentos del evento", HttpStatus.OK, discountService.getByEvent(eventId));
    }

    @PostMapping("/quote")
    public ResponseEntity<GeneralResponse> quote(@Valid @RequestBody PriceQuoteRequest request) {
        return buildResponse("Cotización calculada", HttpStatus.OK, discountService.quote(request));
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity.status(status).body(GeneralResponse.builder()
                .uri(uri).message(message).status(status.value()).time(LocalDateTime.now()).data(data).build());
    }
}