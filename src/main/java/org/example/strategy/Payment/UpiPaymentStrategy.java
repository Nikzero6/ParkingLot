package org.example.strategy.Payment;

import org.example.models.Exceptions;
import org.example.models.PaymentDetails;

public class UpiPaymentStrategy implements PaymentStrategy {
    private final String upiId;
    private int upiPaymentsCount = 0;

    public UpiPaymentStrategy(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public PaymentDetails pay(double amount) throws Exceptions.PaymentFailedException {
        System.out.println("Payment request sent to upiId: " + upiId);
        System.out.println("Payment of amount " + amount + " done successfully");

        String paymentId = "UpiPayment-" + ++upiPaymentsCount;
        
        return new PaymentDetails(paymentId, amount, this);
    }
}
