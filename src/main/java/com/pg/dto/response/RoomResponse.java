package com.pg.dto.response;

import com.pg.entity.enums.RoomStatus;
import com.pg.entity.enums.RoomType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private RoomType type;
    private Integer capacity;
    private Integer currentOccupancy;
    private Integer availableBeds; // capacity - currentOccupancy
    private BigDecimal monthlyRent;
    private Integer floorNumber;
    private String amenities;
    private RoomStatus status;
    private Long pgId;
    private String pgName;
}
