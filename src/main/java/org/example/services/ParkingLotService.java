package org.example.services;

import org.example.models.*;
import org.example.scripts.ParkingLotScripts;
import org.example.strategy.Payment.PaymentStrategy;
import org.example.strategy.Payment.UpiPaymentStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParkingLotService {
    private static final Map<String, ParkingLot> parkingLotRepo = new HashMap<>();

    private final ParkingTicketService parkingTicketService;

    private ParkingLotService() {
        ParkingLot parkingLot = ParkingLotScripts.generateParkingLot();

        System.out.println("Generated parkingLot:\n" + parkingLot);

        parkingLotRepo.put(parkingLot.getId(), parkingLot);

        parkingTicketService = ParkingTicketService.getInstance();
    }

    public static ParkingLotService getInstance() {
        return ParkingLotServiceHelper.INSTANCE;
    }

    public ParkingLot getParkingLot() {
        List<ParkingLot> parkingLots = parkingLotRepo.values().stream().toList();
        return parkingLots.isEmpty() ? null : parkingLots.getFirst();
    }

    public void displayParkingLotStatus() {
        getParkingLot().displayParkingLotStatus();
    }

    public Optional<ParkingTicket> parkVehicle(Vehicle vehicle, String gateId) {
        System.out.println("Park " + vehicle + " at " + gateId);

        ParkingLot parkingLot = getParkingLot();
        Gate gate = parkingLot.getGate(gateId);

        ParkingTicket parkingTicket = null;

        try {
            parkingTicket = parkingTicketService.createParkingTicket(parkingLot, vehicle, gate);
        } catch (Exceptions.WrongGateException | Exceptions.ParkingLotFullException exception) {
            System.out.println(exception.getMessage());
        }

        return Optional.ofNullable(parkingTicket);
    }

    public Optional<ParkingBill> unParkVehicle(ParkingTicket parkingTicket, String gateId) {
        System.out.println("UnPark " + parkingTicket + " at " + gateId);

        Gate gate = getParkingLot().getGate(gateId);
        PaymentStrategy paymentStrategy = new UpiPaymentStrategy("upi@ybl");

        ParkingBill parkingBill = null;

        try {
            parkingBill = parkingTicketService.createParkingBill(
                    parkingTicket, gate, paymentStrategy);
        } catch (Exceptions.WrongGateException | Exceptions.PaymentFailedException exception) {
            System.out.println(exception.getMessage());
        }

        return Optional.ofNullable(parkingBill);
    }

    private static class ParkingLotServiceHelper {
        private static final ParkingLotService INSTANCE = new ParkingLotService();
    }
}

