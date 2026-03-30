package com.order_service.service.workflow;

import com.order_service.dto.RescheduleShipmentRequest;
import com.order_service.dto.ScheduleShipmentRequest;
import com.order_service.service.activity.OrderActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    private static final int DEFAULT_FIXED_DELAY_DAYS = 2;

    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime plannedShipDate;
    private boolean deliveryDateChanged = false;
    private boolean shipped = false;

    @Override
    public void processOrder(Long orderId, String productId, Integer quantity) {

        boolean inventoryReserved = false;
        boolean shipmentScheduled = false;

        try {
            activities.reserveInventory(productId, quantity);
            inventoryReserved = true;

            activities.updateOrderStatus(orderId, "CONFIRMED");

            LocalDateTime workflowNow = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Workflow.currentTimeMillis()),
                ZoneId.systemDefault()
            );


            plannedShipDate = workflowNow.plusDays(DEFAULT_FIXED_DELAY_DAYS);

            ScheduleShipmentRequest shipmentRequest = ScheduleShipmentRequest.builder()
                .orderId(orderId)
                .scheduleType("FIXED_DELAY")
                .expectedDeliveryDate(null)
                .plannedShipDate(plannedShipDate)
                .build();

            activities.scheduleShipment(shipmentRequest);
            shipmentScheduled = true;

            while (!shipped) {

                LocalDateTime now = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(Workflow.currentTimeMillis()),
                    ZoneId.systemDefault()
                );


                LocalDateTime activeTargetDate =
                    (expectedDeliveryDate != null) ? expectedDeliveryDate : plannedShipDate;

                Duration waitDuration = Duration.between(now, activeTargetDate);

                if (waitDuration.isNegative() || waitDuration.isZero()) {
                    activities.markShipmentAsShipped(orderId);
                    activities.updateOrderStatus(orderId, "SHIPPED");
                    shipped = true;
                    break;
                }

                boolean signalReceived = Workflow.await(waitDuration, () -> deliveryDateChanged);

                if (signalReceived) {
                    deliveryDateChanged = false;

                    if (expectedDeliveryDate == null) {
                        throw ApplicationFailure.newNonRetryableFailure(
                            "Expected delivery date cannot be null when delivery date is updated",
                            "INVALID_DELIVERY_DATE"
                        );
                    }

                    RescheduleShipmentRequest request = RescheduleShipmentRequest.builder()
                        .scheduleType("DELIVERY_BASED")
                        .expectedDeliveryDate(expectedDeliveryDate)
                        .plannedShipDate(plannedShipDate)
                        .reason("Customer changed delivery date")
                        .build();

                    activities.rescheduleShipment(orderId, request);
                }
            }

        } catch (ActivityFailure e) {

            String failureType = getFailureType(e);

            if (inventoryReserved && !shipmentScheduled) {
                try {
                    activities.releaseInventory(productId, quantity);
                } catch (Exception compensationEx) {
                    activities.updateOrderStatusWithReason(
                        orderId,
                        "MANUAL_REVIEW",
                        "COMPENSATION_FAILED"
                    );
                    throw compensationEx;
                }
            }

            activities.updateOrderStatusWithReason(
                orderId,
                "MANUAL_REVIEW",
                failureType
            );

            throw e;

        } catch (Exception e) {

            if (inventoryReserved && !shipmentScheduled) {
                try {
                    activities.releaseInventory(productId, quantity);
                } catch (Exception compensationEx) {
                    activities.updateOrderStatusWithReason(
                        orderId,
                        "MANUAL_REVIEW",
                        "COMPENSATION_FAILED"
                    );
                    throw compensationEx;
                }
            }

            activities.updateOrderStatusWithReason(
                orderId,
                "MANUAL_REVIEW",
                "ORDER_PROCESSING_FAILED"
            );

            throw e;
        }
    }

    @Override
    public void updateDeliveryDate(LocalDateTime newExpectedDeliveryDate) {
        this.expectedDeliveryDate = newExpectedDeliveryDate;
        this.deliveryDateChanged = true;
    }

    private String getFailureType(ActivityFailure e) {
        Throwable cause = e.getCause();

        if (cause instanceof ApplicationFailure applicationFailure) {
            return applicationFailure.getType();
        }

        return "ORDER_PROCESSING_FAILED";
    }
}