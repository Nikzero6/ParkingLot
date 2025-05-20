package org.example.strategy.Payment;

import org.example.models.Exceptions;
import org.example.models.PaymentDetails;

public interface PaymentStrategy {
    PaymentDetails pay(double amount) throws Exceptions.PaymentFailedException;
}
