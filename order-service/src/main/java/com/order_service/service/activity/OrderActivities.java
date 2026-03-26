package com.order_service.service.activity;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {

    void reserveInventory(String productId, Integer quantity);

    void releaseInventory(String productId, Integer quantity);

    void createShipment(Long orderId);

    void updateOrderStatus(Long orderId, String status);
}
