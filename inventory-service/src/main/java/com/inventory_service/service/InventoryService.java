package com.inventory_service.service;

import com.inventory_service.entity.Inventory;
import com.inventory_service.exception.InsufficientInventoryException;
import com.inventory_service.exception.ProductNotFoundException;
import com.inventory_service.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory addInventory(String productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElse(null);

        if (inventory == null) {
            inventory = Inventory.builder()
                .productId(productId)
                .availableQuantity(quantity)
                .build();
        } else {
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        }

        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public String reserveInventory(String productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() ->
                new ProductNotFoundException("Product not found: " + productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientInventoryException(
                "Insufficient inventory for product: " + productId
            );
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventoryRepository.save(inventory);

        return "Inventory reserved successfully";
    }

    public String releaseInventory(String productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() ->
                new ProductNotFoundException("Product not found: " + productId));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventoryRepository.save(inventory);

        return "Inventory released successfully";
    }
}