package com.pg.dto.response;

import com.pg.entity.enums.RentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class RentRecordResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String roomNumber;
    private Integer month;
    private Integer year;
    private BigDecimal baseAmount;
    private BigDecimal penaltyAmount;
    private BigDecimal totalAmount;
    private LocalDate dueDate;
    private RentStatus status;
    private LocalDateTime paidAt;
    private String notes;
    private LocalDateTime createdAt;
}
