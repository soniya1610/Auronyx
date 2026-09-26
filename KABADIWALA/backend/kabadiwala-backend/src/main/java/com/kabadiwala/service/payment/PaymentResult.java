package com.kabadiwala.service.payment;

import java.math.BigDecimal;

public class PaymentResult {
    private final boolean success;
    private final String providerReference;
    private final String message;
    private final BigDecimal amount;

    public PaymentResult(boolean success, String providerReference, String message, BigDecimal amount) {
        this.success = success;
        this.providerReference = providerReference;
        this.message = message;
        this.amount = amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
