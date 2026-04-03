package com.pg.dto.request;

import com.pg.entity.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType type;

    @NotNull @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @Positive(message = "Monthly rent must be positive")
    private BigDecimal monthlyRent;

    private Integer floorNumber;
    private String amenities;
}
