package com.pg.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data @Builder
public class DashboardResponse {
    private String pgName;
    private Long totalRooms;
    private Long availableRooms;
    private Long fullyOccupiedRooms;
    private Long activeTenants;
    private Long openComplaints;
    private Long pendingRentCount;
    private BigDecimal totalOutstandingRent;
    private Long overdueRentCount;
}
