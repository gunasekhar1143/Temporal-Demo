package com.inventory_service.controller;

import com.inventory_service.entity.Inventory;
import com.inventory_service.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/add")
    public Inventory addInventory(@RequestParam String productId,
        @RequestParam Integer quantity) {

        return inventoryService.addInventory(productId, quantity);
    }

    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @PostMapping("/reserve")
    public String reserveInventory(@RequestParam String productId,
        @RequestParam Integer quantity) {

        return inventoryService.reserveInventory(productId, quantity);
    }

    @PostMapping("/release")
    public String releaseInventory(@RequestParam String productId,
        @RequestParam Integer quantity) {

        return inventoryService.releaseInventory(productId, quantity);
    }
}