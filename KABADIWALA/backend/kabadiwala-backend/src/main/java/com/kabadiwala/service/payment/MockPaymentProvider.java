package com.kabadiwala.service.payment;

import com.kabadiwala.entity.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentProvider implements PaymentProvider {

    @Override
    public PaymentResult processPayment(String transactionRef, BigDecimal amount, Payment.Method method) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentResult(false, null, "Invalid payment amount: " + amount, amount);
        }
        String ref = "MOCK-PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return new PaymentResult(true, ref, "Payment processed successfully via MockPaymentProvider", amount);
    }

    @Override
    public boolean supports(Payment.Method method) {
        return true;
    }
}
