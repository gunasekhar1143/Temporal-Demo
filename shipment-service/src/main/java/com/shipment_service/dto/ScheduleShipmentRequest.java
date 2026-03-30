package com.shipment_service.dto;

import com.shipment_service.entity.ShipmentScheduleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleShipmentRequest {

    private Long orderId;
    private ShipmentScheduleType scheduleType;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime plannedShipDate;
}
