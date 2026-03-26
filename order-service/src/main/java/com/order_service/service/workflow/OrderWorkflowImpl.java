package com.order_service.service.workflow;

import com.order_service.service.activity.OrderActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class OrderWorkflowImpl implements OrderWorkflow {

    private final OrderActivities activities =
        Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(10))
                .setRetryOptions(
                    RetryOptions.newBuilder()
                        .setMaximumAttempts(3)
                        .build()
                )
                .build()
        );

    @Override
    public void processOrder(Long orderId, String productId, Integer quantity) {

        boolean inventoryReserved = false;

        try {
            activities.reserveInventory(productId, quantity);
            inventoryReserved = true;

            activities.createShipment(orderId);

            activities.updateOrderStatus(orderId, "CONFIRMED");

        } catch (Exception e) {

            if (inventoryReserved) {
                activities.releaseInventory(productId, quantity);
            }

            activities.updateOrderStatus(orderId, "FAILED");

            throw e;
        }
    }
}