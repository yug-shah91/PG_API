package com.pg.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PGRequest {
    @NotBlank(message = "PG name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private String description;
    private String contactPhone;

    @NotNull(message = "Base monthly rent is required")
    @Positive(message = "Rent must be positive")
    private BigDecimal baseMonthlyRent;

    private Integer rentDueDay;
    private Integer penaltyPercent;
}
