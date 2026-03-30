package com.order_service.service.workflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.time.LocalDateTime;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowMethod
    void processOrder(Long orderId, String productId, Integer quantity);

    @SignalMethod
    void updateDeliveryDate(LocalDateTime newExpectedDeliveryDate);
}
