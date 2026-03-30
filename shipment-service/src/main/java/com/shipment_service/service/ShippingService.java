package com.shipment_service.service;

import com.shipment_service.entity.Shipment;
import com.shipment_service.entity.ShipmentScheduleType;
import com.shipment_service.entity.ShipmentStatus;
import com.shipment_service.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShippingService {

    private final ShipmentRepository shipmentRepository;

    public ShippingService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment scheduleShipment(Long orderId,
        ShipmentScheduleType scheduleType,
        LocalDateTime expectedDeliveryDate,
        LocalDateTime plannedShipDate) {

        if (shipmentRepository.findByOrderId(orderId).isPresent()) {
            throw new RuntimeException("Shipment already exists for order: " + orderId);
        }

        Shipment shipment = Shipment.builder()
            .orderId(orderId)
            .status(ShipmentStatus.SCHEDULED)
            .scheduleType(scheduleType)
            .expectedDeliveryDate(expectedDeliveryDate)
            .plannedShipDate(plannedShipDate)
            .rescheduled(false)
            .build();

        return shipmentRepository.save(shipment);
    }

    public Shipment rescheduleShipment(Long orderId,
        ShipmentScheduleType scheduleType,
        LocalDateTime expectedDeliveryDate,
        LocalDateTime plannedShipDate,
        String reason) {

        Shipment shipment = shipmentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Shipment not found for order: " + orderId));

        shipment.setScheduleType(scheduleType);
        shipment.setExpectedDeliveryDate(expectedDeliveryDate);
        shipment.setPlannedShipDate(plannedShipDate);
        shipment.setRescheduled(true);
        shipment.setRescheduleReason(reason);
        shipment.setStatus(ShipmentStatus.SCHEDULED);

        return shipmentRepository.save(shipment);
    }

    public Shipment markAsShipped(Long orderId) {

        Shipment shipment = shipmentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Shipment not found for order: " + orderId));

        shipment.setStatus(ShipmentStatus.SHIPPED);

        return shipmentRepository.save(shipment);
    }

    public Shipment cancelShipment(Long orderId) {

        Shipment shipment = shipmentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Shipment not found for order: " + orderId));

        shipment.setStatus(ShipmentStatus.CANCELLED);

        return shipmentRepository.save(shipment);
    }

    public Shipment getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Shipment not found for order: " + orderId));
    }
}