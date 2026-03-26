package com.order_service.controller;


import com.order_service.config.TemporalConfig;
import com.order_service.entity.Order;
import com.order_service.entity.OrderStatus;
import com.order_service.service.OrderService;
import com.order_service.service.workflow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final WorkflowClient workflowClient;

    public OrderController(OrderService orderService,WorkflowClient workflowClient) {
        this.orderService = orderService;
        this.workflowClient=workflowClient;
    }

    @PostMapping
    public String createOrder(@RequestBody Order order) {

        Order savedOrder = orderService.createOrder(order);

        WorkflowOptions options = WorkflowOptions.newBuilder()
            .setTaskQueue(TemporalConfig.TASK_QUEUE)
            .setWorkflowId("order-" + savedOrder.getId())
            .build();

        OrderWorkflow workflow =
            workflowClient.newWorkflowStub(OrderWorkflow.class, options);

        WorkflowClient.start(
            workflow::processOrder,
            savedOrder.getId(),
            savedOrder.getProductId(),
            savedOrder.getQuantity()
        );

        return "Order created and workflow started with id: " + savedOrder.getId();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id,
        @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }
}



















//public String createOrder(@RequestBody Order order) {
//
//    Order savedOrder = orderService.createOrder(order);
//
//    WorkflowOptions options = WorkflowOptions.newBuilder()
//        .setTaskQueue(TemporalConfig.TASK_QUEUE)
//        .setWorkflowId("order-" + savedOrder.getId())
//        .build();
//
//    OrderWorkflow workflow =
//        workflowClient.newWorkflowStub(OrderWorkflow.class, options);
//
//    WorkflowClient.start(
//        workflow::processOrder,
//        savedOrder.getId(),
//        savedOrder.getProductId(),
//        savedOrder.getQuantity()
//    );
//
//    return "Order created and workflow started with id: " + savedOrder.getId();
//}