package com.pg.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class TenantResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Long roomId;
    private String roomNumber;
    private Long pgId;
    private String pgName;
    private LocalDate joinDate;
    private LocalDate expectedLeaveDate;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String idProofType;
    private Boolean isActive;
    private LocalDateTime registeredAt;
}
