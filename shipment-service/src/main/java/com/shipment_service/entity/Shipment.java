package com.shipment_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One shipment per order (for your current design)
    @Column(nullable = false, unique = true)
    private Long orderId;

    // Current shipment status
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    // Which rule is currently used for scheduling
    @Enumerated(EnumType.STRING)
    private ShipmentScheduleType scheduleType;

    // Expected delivery date (used for DELIVERY_BASED logic)
    private LocalDateTime expectedDeliveryDate;

    // When shipment is planned to happen
    private LocalDateTime plannedShipDate;

    // Whether shipment was rescheduled
    private Boolean rescheduled;

    // Optional reason for rescheduling
    private String rescheduleReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Automatically set timestamps
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.rescheduled == null) {
            this.rescheduled = false;
        }

        if (this.status == null) {
            this.status = ShipmentStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}