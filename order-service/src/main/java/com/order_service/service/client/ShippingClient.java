package com.order_service.service.client;

import com.order_service.dto.RescheduleShipmentRequest;
import com.order_service.dto.ScheduleShipmentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "shipment-service", url = "http://localhost:8083")
public interface ShippingClient {

    @PostMapping("/shipping/schedule")
    String scheduleShipment(@RequestBody ScheduleShipmentRequest request);

    @PutMapping("/shipping/reschedule/{orderId}")
    String rescheduleShipment(@PathVariable Long orderId,
        @RequestBody RescheduleShipmentRequest request);

    @PutMapping("/shipping/ship/{orderId}")
    String markAsShipped(@PathVariable Long orderId);

    @PutMapping("/shipping/cancel/{orderId}")
    String cancelShipment(@PathVariable Long orderId);
}