package com.shipment_service.controller;

import com.shipment_service.dto.RescheduleShipmentRequest;
import com.shipment_service.dto.ScheduleShipmentRequest;
import com.shipment_service.entity.Shipment;
import com.shipment_service.service.ShippingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/schedule")
    public Shipment scheduleShipment(@RequestBody ScheduleShipmentRequest request) {
        return shippingService.scheduleShipment(
            request.getOrderId(),
            request.getScheduleType(),
            request.getExpectedDeliveryDate(),
            request.getPlannedShipDate()
        );
    }

    @PutMapping("/reschedule/{orderId}")
    public Shipment rescheduleShipment(@PathVariable Long orderId,
        @RequestBody RescheduleShipmentRequest request) {
        return shippingService.rescheduleShipment(
            orderId,
            request.getScheduleType(),
            request.getExpectedDeliveryDate(),
            request.getPlannedShipDate(),
            request.getReason()
        );
    }

    @PutMapping("/ship/{orderId}")
    public Shipment markAsShipped(@PathVariable Long orderId) {
        return shippingService.markAsShipped(orderId);
    }

    @PutMapping("/cancel/{orderId}")
    public Shipment cancelShipment(@PathVariable Long orderId) {
        return shippingService.cancelShipment(orderId);
    }

    @GetMapping("/{orderId}")
    public Shipment getShipmentByOrderId(@PathVariable Long orderId) {
        return shippingService.getShipmentByOrderId(orderId);
    }
}