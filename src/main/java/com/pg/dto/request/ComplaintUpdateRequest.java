package com.pg.dto.request;

import com.pg.entity.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintUpdateRequest {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    private String ownerResponse;
}
