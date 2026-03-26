package com.order_service.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "http://localhost:8082")
public interface InventoryClient {

    @PostMapping("/inventory/reserve")
    String reserve(@RequestParam String productId,
        @RequestParam Integer quantity);

    @PostMapping("/inventory/release")
    String release(@RequestParam String productId,
        @RequestParam Integer quantity);
}
