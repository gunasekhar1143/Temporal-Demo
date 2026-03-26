package com.order_service.service.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowMethod
    void processOrder(Long orderId, String productId, Integer quantity);
}
