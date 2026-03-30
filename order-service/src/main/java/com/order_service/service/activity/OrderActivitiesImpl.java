package com.order_service.service.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order_service.dto.RescheduleShipmentRequest;
import com.order_service.dto.ScheduleShipmentRequest;
import com.order_service.entity.OrderStatus;
import com.order_service.exception.ErrorResponse;
import com.order_service.service.OrderService;
import com.order_service.service.client.InventoryClient;
import com.order_service.service.client.ShippingClient;
import feign.FeignException;
import io.temporal.failure.ApplicationFailure;
import org.springframework.stereotype.Component;

@Component
public class OrderActivitiesImpl implements OrderActivities {

    private final InventoryClient inventoryClient;
    private final ShippingClient shippingClient;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderActivitiesImpl(InventoryClient inventoryClient,
        ShippingClient shippingClient,
        OrderService orderService,
        ObjectMapper objectMapper) {
        this.inventoryClient = inventoryClient;
        this.shippingClient = shippingClient;
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void reserveInventory(String productId, Integer quantity) {
        try {
            inventoryClient.reserve(productId, quantity);

        } catch (FeignException e) {

            String errorCode = extractErrorCode(e);

            if ("PRODUCT_NOT_FOUND".equals(errorCode)) {
                throw ApplicationFailure.newNonRetryableFailure(
                    "Product not found for productId: " + productId,
                    "PRODUCT_NOT_FOUND"
                );
            }

            if ("OUT_OF_STOCK".equals(errorCode)) {
                throw ApplicationFailure.newNonRetryableFailure(
                    "Insufficient inventory for productId: " + productId,
                    "OUT_OF_STOCK"
                );
            }

            throw ApplicationFailure.newFailure(
                "Inventory service temporary failure",
                "INVENTORY_TEMPORARY_FAILURE"
            );

        } catch (Exception e) {
            throw ApplicationFailure.newFailure(
                "Unexpected inventory failure",
                "INVENTORY_UNKNOWN_FAILURE"
            );
        }
    }

    @Override
    public void releaseInventory(String productId, Integer quantity) {
        try {
            inventoryClient.release(productId, quantity);

        } catch (FeignException e) {
            throw ApplicationFailure.newFailure(
                "Inventory release failed",
                "INVENTORY_RELEASE_FAILURE"
            );

        } catch (Exception e) {
            throw ApplicationFailure.newFailure(
                "Unexpected inventory release failure",
                "INVENTORY_RELEASE_UNKNOWN_FAILURE"
            );
        }
    }

    @Override
    public void scheduleShipment(ScheduleShipmentRequest request) {
        try {
            shippingClient.scheduleShipment(request);

        } catch (FeignException e) {
            throw ApplicationFailure.newFailure(
                "Shipment scheduling failed for orderId: " + request.getOrderId(),
                "SHIPMENT_SCHEDULE_FAILURE"
            );

        } catch (Exception e) {
            throw ApplicationFailure.newFailure(
                "Unexpected shipment scheduling failure for orderId: " + request.getOrderId(),
                "SHIPMENT_SCHEDULE_UNKNOWN_FAILURE"
            );
        }
    }

    @Override
    public void rescheduleShipment(Long orderId, RescheduleShipmentRequest request) {
        try {
            shippingClient.rescheduleShipment(orderId, request);

        } catch (FeignException e) {
            throw ApplicationFailure.newFailure(
                "Shipment rescheduling failed for orderId: " + orderId,
                "SHIPMENT_RESCHEDULE_FAILURE"
            );

        } catch (Exception e) {
            throw ApplicationFailure.newFailure(
                "Unexpected shipment rescheduling failure for orderId: " + orderId,
                "SHIPMENT_RESCHEDULE_UNKNOWN_FAILURE"
            );
        }
    }

    @Override
    public void markShipmentAsShipped(Long orderId) {
        try {
            shippingClient.markAsShipped(orderId);

        } catch (FeignException e) {
            throw ApplicationFailure.newFailure(
                "Mark shipment as shipped failed for orderId: " + orderId,
                "SHIPMENT_MARK_SHIPPED_FAILURE"
            );

        } catch (Exception e) {
            throw ApplicationFailure.newFailure(
                "Unexpected mark shipment as shipped failure for orderId: " + orderId,
                "SHIPMENT_MARK_SHIPPED_UNKNOWN_FAILURE"
            );
        }
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        try {
            orderService.updateOrderStatus(orderId, OrderStatus.valueOf(status));
        } catch (Exception e) {
            throw ApplicationFailure.newNonRetryableFailure(
                "Failed to update order status for orderId: " + orderId,
                "ORDER_STATUS_UPDATE_FAILED"
            );
        }
    }

    @Override
    public void updateOrderStatusWithReason(Long orderId, String status, String reason) {
        try {
            orderService.updateOrderStatusAndFailureReason(
                orderId,
                OrderStatus.valueOf(status),
                reason
            );
        } catch (Exception e) {
            throw ApplicationFailure.newNonRetryableFailure(
                "Failed to update order status with reason for orderId: " + orderId,
                "ORDER_STATUS_UPDATE_FAILED"
            );
        }
    }

    private String extractErrorCode(FeignException e) {
        try {
            String responseBody = e.contentUTF8();

            if (responseBody == null || responseBody.isBlank()) {
                return null;
            }

            ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
            return errorResponse.getErrorCode();

        } catch (Exception ex) {
            return null;
        }
    }
}