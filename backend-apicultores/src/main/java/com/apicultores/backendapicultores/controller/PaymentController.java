package com.apicultores.backendapicultores.controller;

import com.apicultores.backendapicultores.domain.dto.request.PaymentRequest;
import com.apicultores.backendapicultores.domain.dto.response.GeneralResponse;
import com.apicultores.backendapicultores.domain.entity.Payment;
import com.apicultores.backendapicultores.repository.PaymentRepository;
import com.apicultores.backendapicultores.service.CheckoutService;
import com.apicultores.backendapicultores.service.PaymentService;
import com.apicultores.backendapicultores.service.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
//@CrossOrigin(origins = "http://localhost:5173") Aún no implementado en front
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<GeneralResponse> createPayment(@RequestBody PaymentRequest request) {
        return buildResponse(
                "Pago registrado exitosamente",
                HttpStatus.CREATED,
                paymentService.createPayment(request)
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> getAllPayments() {
        return buildResponse(
                "Se han obtenido los pagos",
                HttpStatus.OK,
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<GeneralResponse> getPaymentById(@PathVariable UUID paymentId) {
        return buildResponse(
                "Se ha obtenido el pago",
                HttpStatus.OK,
                paymentService.getPaymentById(paymentId)
        );
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<GeneralResponse> getPaymentByReservationId(@PathVariable UUID reservationId) {
        return buildResponse(
                "Se ha obtenido el pago de la reserva",
                HttpStatus.OK,
                paymentService.getPaymentByReservationId(reservationId)
        );
    }

    @GetMapping("/checkout/{paymentId}")
    public ResponseEntity<Void> redirectToStripeCheckout(@PathVariable UUID paymentId) throws StripeException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        String checkoutUrl = stripeService.createCheckoutSession(payment);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(checkoutUrl));
        return new ResponseEntity<>(headers,HttpStatus.SEE_OTHER);
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("payment_id") UUID paymentId) {
        checkoutService.confirmCheckout(paymentId);
        return "¡Pago procesado con éxito!";
    }

    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam("payment_id") UUID paymentId) {
        return "El pago fue cancelado por el usuario";
    }

    private ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity
                .status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build());
    }
}
