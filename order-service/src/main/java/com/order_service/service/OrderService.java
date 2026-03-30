package com.order_service.service;

import com.order_service.entity.Order;
import com.order_service.entity.OrderStatus;
import com.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        order.setFailureReason(null);
        order.setRetryCount(0);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updateOrderStatusAndFailureReason(Long id, OrderStatus status, String failureReason) {
        Order order = getOrderById(id);
        order.setStatus(status);
        order.setFailureReason(failureReason);
        return orderRepository.save(order);
    }

    public Order moveToManualReview(Long id, String failureReason) {
        Order order = getOrderById(id);
        order.setStatus(OrderStatus.MANUAL_REVIEW);
        order.setFailureReason(failureReason);
        return orderRepository.save(order);
    }

    public Order prepareOrderForManualRetry(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus() != OrderStatus.MANUAL_REVIEW &&
            order.getStatus() != OrderStatus.FAILED) {
            throw new RuntimeException("Only FAILED or MANUAL_REVIEW orders can be retried");
        }

        order.setStatus(OrderStatus.PENDING);
        order.setFailureReason(null);
        order.setRetryCount(order.getRetryCount() + 1);

        return orderRepository.save(order);
    }
}