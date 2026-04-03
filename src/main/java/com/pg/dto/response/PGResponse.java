package com.pg.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class PGResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String description;
    private String contactPhone;
    private BigDecimal baseMonthlyRent;
    private Integer rentDueDay;
    private Integer penaltyPercent;
    private Boolean isActive;
    private String ownerName;
    private String ownerEmail;
    private Integer totalRooms;
    private Integer availableRooms;
    private Long activeTenants;
    private LocalDateTime createdAt;
}
