package com.order_service.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "shipping-service", url = "http://localhost:8083")
public interface ShippingClient {

    @PostMapping("/shipping/create")
    String createShipment(@RequestParam Long orderId);

    @PostMapping("/shipping/cancel")
    String cancelShipment(@RequestParam Long orderId);
}
