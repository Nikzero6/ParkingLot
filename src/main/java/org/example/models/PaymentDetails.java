package org.example.models;

import org.example.strategy.Payment.PaymentStrategy;

public record PaymentDetails(String id, double amount, PaymentStrategy paymentStrategy) {
}
