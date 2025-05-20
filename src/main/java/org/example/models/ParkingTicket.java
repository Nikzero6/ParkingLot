package org.example.models;

import java.util.Date;

public record ParkingTicket(String id, Vehicle vehicle, ParkingSpot parkingSpot, Date inTime) {
}
