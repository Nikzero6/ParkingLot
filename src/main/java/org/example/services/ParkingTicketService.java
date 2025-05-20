package org.example.services;

import org.example.models.*;
import org.example.models.enums.GateType;
import org.example.models.enums.ParkingSpotStatus;
import org.example.models.enums.VehicleType;
import org.example.strategy.Payment.PaymentStrategy;

import java.util.*;

public class ParkingTicketService {
    private final Map<String, ParkingTicket> parkingTicketRepo;

    private int parkingTicketsCount = 0;
    private int parkingBillsCount = 0;

    private ParkingTicketService() {
        parkingTicketRepo = new HashMap<>();
    }

    public static ParkingTicketService getInstance() {
        return ParkingTicketServiceHelper.INSTANCE;
    }

    public List<ParkingTicket> getParkingTickets() {
        return parkingTicketRepo.values().stream().toList();
    }

    public ParkingTicket createParkingTicket(ParkingLot parkingLot, Vehicle vehicle, Gate gate) throws Exceptions.WrongGateException, Exceptions.ParkingLotFullException {
        if (gate.type() == GateType.EXIT) {
            throw new Exceptions.WrongGateException(GateType.EXIT, GateType.ENTRY);
        }

        Optional<ParkingFloor> optionalParkingFloor = parkingLot.findParkingFloor(
                vehicle.type(), gate);

        if (optionalParkingFloor.isEmpty()) {
            throw new Exceptions.ParkingLotFullException();
        }

        ParkingFloor parkingFloor = optionalParkingFloor.get();

        Optional<ParkingSpot> optionalParkingSpot = parkingFloor.findParkingSpot(vehicle.type());

        if (optionalParkingSpot.isEmpty()) {
            throw new Exceptions.ParkingLotFullException();
        }

        ParkingSpot parkingSpot = optionalParkingSpot.get();

        parkingSpot.setParkingSpotStatus(ParkingSpotStatus.OCCUPIED);

        System.out.println(
                vehicle.regNumber() + " is parked at floor " + parkingFloor.getId() + ", and " + parkingSpot);

        return generateParkingTicket(vehicle, parkingSpot);
    }

    public ParkingBill createParkingBill(ParkingTicket parkingTicket, Gate gate, PaymentStrategy paymentStrategy) throws Exceptions.WrongGateException, Exceptions.PaymentFailedException {
        if (gate.type() == GateType.ENTRY) {
            throw new Exceptions.WrongGateException(GateType.ENTRY, GateType.EXIT);
        }

        Date outTime = new Date();

        double amount = calculateCharges(
                parkingTicket.vehicle().type(), parkingTicket.inTime(), outTime);

        PaymentDetails paymentDetails = paymentStrategy.pay(amount);

        parkingTicket.parkingSpot().setParkingSpotStatus(ParkingSpotStatus.AVAILABLE);

        return generateParkingBill(parkingTicket, outTime, paymentDetails);
    }

    private ParkingBill generateParkingBill(ParkingTicket parkingTicket, Date outTime, PaymentDetails paymentDetails) {
        parkingBillsCount++;

        String parkingBillId = "ParkingBill-" + parkingBillsCount;

        return new ParkingBill(parkingBillId, parkingTicket, outTime, paymentDetails);
    }

    private double calculateCharges(VehicleType vehicleType, Date inTime, Date outTime) {
        double baseCharge = vehicleType.getBaseCharge();
        double hourlyRate = vehicleType.getHourlyRate();

        long milliSeconds = outTime.getTime() - inTime.getTime();
        int hours = (int) Math.ceil((double) milliSeconds / (1000 * 60 * 60));

        double hourlyCharge = hourlyRate * hours;

        return baseCharge + hourlyCharge;
    }

    private ParkingTicket generateParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot) {
        parkingTicketsCount++;

        String parkingTicketId = "ParkingTicket-" + parkingTicketsCount;

        ParkingTicket parkingTicket = new ParkingTicket(
                parkingTicketId, vehicle, parkingSpot, new Date());

        parkingTicketRepo.put(parkingTicketId, parkingTicket);

        return parkingTicket;
    }

    private static class ParkingTicketServiceHelper {
        private static final ParkingTicketService INSTANCE = new ParkingTicketService();
    }
}
