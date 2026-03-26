package com.shipment_service.service;


import com.shipment_service.entity.Shipment;
import com.shipment_service.repository.ShipmentRepository;
import com.shipment_service.entity.ShipmentStatus;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {

    private final ShipmentRepository shipmentRepository;

    public ShippingService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment createShipment(Long orderId) {

        if (orderId == 999L) {
            throw new RuntimeException("Shipment creation failed for order: " + orderId);
        }

        Shipment shipment = Shipment.builder()
            .orderId(orderId)
            .status(ShipmentStatus.CREATED)
            .build();

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
