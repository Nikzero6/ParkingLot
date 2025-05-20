package org.example.models;

import java.util.Date;

public record ParkingBill(String id, ParkingTicket parkingTicket, Date outTime,
                          PaymentDetails paymentDetails) {
}
