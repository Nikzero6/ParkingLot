package org.example.models;

import org.example.models.enums.GateType;

public class Exceptions {
    public static class ParkingLotFullException extends Exception {
        public ParkingLotFullException() {
            super("Very sorry, parking lot is full");
        }
    }

    public static class WrongGateException extends Exception {
        public WrongGateException(GateType wrongGate, GateType rightGate) {
            super("Please, go to " + rightGate + ", you are on " + wrongGate);
        }
    }

    public static class PaymentFailedException extends Exception {
        public PaymentFailedException() {
            super("Payment failed, please try again");
        }
    }
}
