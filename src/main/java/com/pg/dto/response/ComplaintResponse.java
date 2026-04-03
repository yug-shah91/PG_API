package com.pg.dto.response;

import com.pg.entity.enums.ComplaintCategory;
import com.pg.entity.enums.ComplaintStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ComplaintResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String roomNumber;
    private Long pgId;
    private ComplaintCategory category;
    private String title;
    private String description;
    private ComplaintStatus status;
    private String ownerResponse;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
