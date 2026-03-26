package com.shipment_service.controller;

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

    @PostMapping("/create")
    public Shipment createShipment(@RequestParam Long orderId) {
        return shippingService.createShipment(orderId);
    }

    @PostMapping("/cancel")
    public Shipment cancelShipment(@RequestParam Long orderId) {
        return shippingService.cancelShipment(orderId);
    }

    @GetMapping("/{orderId}")
    public Shipment getShipmentByOrderId(@PathVariable Long orderId) {
        return shippingService.getShipmentByOrderId(orderId);
    }
}