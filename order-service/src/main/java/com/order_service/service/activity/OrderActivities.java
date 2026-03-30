package com.order_service.service.activity;

import com.order_service.dto.RescheduleShipmentRequest;
import com.order_service.dto.ScheduleShipmentRequest;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {

    void reserveInventory(String productId, Integer quantity);

    void releaseInventory(String productId, Integer quantity);

    void scheduleShipment(ScheduleShipmentRequest request);

    void rescheduleShipment(Long orderId, RescheduleShipmentRequest request);

    void markShipmentAsShipped(Long orderId);

    void updateOrderStatus(Long orderId, String status);

    void updateOrderStatusWithReason(Long orderId, String status, String reason);
}