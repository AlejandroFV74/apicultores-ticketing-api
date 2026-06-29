package com.apicultores.backendapicultores.service;

import com.apicultores.backendapicultores.domain.entity.Payment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @PostConstruct
    public void init(){
        Stripe.apiKey = secretKey;
    }

    public String createCheckoutSession(Payment payment) throws StripeException {
        //Manejo de dinero de stripe (Centavos)
        long amountInCents = payment.getAmount().multiply(new java.math.BigDecimal(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?payment_id=" + payment.getPaymentId())
                .setCancelUrl(cancelUrl + "?payment_id=" + payment.getPaymentId())
                //Producto en Stripe
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Entradas para el Evento")
                                                                .setDescription("Reserva N°: " + payment.getReservation().getReservationId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("payment_id", payment.getPaymentId().toString())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
