package com.order_service.entity;

public enum OrderStatus {

    PENDING,        // order created, not yet processed
    CONFIRMED,      // inventory reserved, order accepted
    SHIPPED,        // shipment completed
    DELIVERED,      // final delivery done

    FAILED,         // system failure
    MANUAL_REVIEW   // requires manual intervention
}