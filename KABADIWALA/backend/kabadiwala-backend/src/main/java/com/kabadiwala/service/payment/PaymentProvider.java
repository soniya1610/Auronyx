package com.kabadiwala.service.payment;

import com.kabadiwala.entity.Payment;

import java.math.BigDecimal;

public interface PaymentProvider {
    PaymentResult processPayment(String transactionRef, BigDecimal amount, Payment.Method method);
    boolean supports(Payment.Method method);
}
