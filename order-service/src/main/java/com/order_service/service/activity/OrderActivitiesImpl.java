package com.order_service.service.activity;

import com.order_service.service.client.InventoryClient;
import com.order_service.service.client.ShippingClient;
import com.order_service.entity.OrderStatus;
import com.order_service.service.OrderService;
import org.springframework.stereotype.Component;

@Component
public class OrderActivitiesImpl implements OrderActivities {

    private final InventoryClient inventoryClient;
    private final ShippingClient shippingClient;
    private final OrderService orderService;

    public OrderActivitiesImpl(InventoryClient inventoryClient,
        ShippingClient shippingClient,
        OrderService orderService) {
        this.inventoryClient = inventoryClient;
        this.shippingClient = shippingClient;
        this.orderService = orderService;
    }

    @Override
    public void reserveInventory(String productId, Integer quantity) {
        inventoryClient.reserve(productId, quantity);
    }

    @Override
    public void releaseInventory(String productId, Integer quantity) {
        inventoryClient.release(productId, quantity);
    }

    @Override
    public void createShipment(Long orderId) {
        shippingClient.createShipment(orderId);
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        orderService.updateOrderStatus(orderId, OrderStatus.valueOf(status));
    }
}
