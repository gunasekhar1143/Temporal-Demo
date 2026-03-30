package com.order_service.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescheduleShipmentRequest {

    private String scheduleType;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime plannedShipDate;
    private String reason;
}