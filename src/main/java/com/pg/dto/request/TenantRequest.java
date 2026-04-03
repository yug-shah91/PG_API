package com.pg.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TenantRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank @Email(message = "Valid email required")
    private String email;

    @NotBlank @Size(min = 6)
    private String password;

    private String phone;

    private Long roomId;

    private LocalDate joinDate;
    private LocalDate expectedLeaveDate;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String idProofType;
    private String idProofNumber;
}
