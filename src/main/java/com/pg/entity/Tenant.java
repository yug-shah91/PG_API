package com.pg.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenants", indexes = {
        @Index(name = "idx_tenant_pg", columnList = "pg_id"),
        @Index(name = "idx_tenant_room", columnList = "room_id"),
        @Index(name = "idx_tenant_user", columnList = "user_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One User → One Tenant. FK 'user_id' in tenants table.
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Which room they live in. Nullable — tenant might be registered but not yet allocated.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @ToString.Exclude
    private Room room;

    // Direct link to PG for fast queries (denormalization)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pg_id", nullable = false)
    @ToString.Exclude
    private PG pg;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "expected_leave_date")
    private LocalDate expectedLeaveDate;

    // Emergency contact details
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 15)
    private String emergencyContactPhone;

    // ID proof details (Aadhar, PAN, Passport etc.)
    @Column(name = "id_proof_type", length = 50)
    private String idProofType;

    @Column(name = "id_proof_number", length = 50)
    private String idProofNumber;

    // Soft delete — don't delete tenants, just mark inactive
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Rent records for this tenant
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<RentRecord> rentRecords = new ArrayList<>();

    // Complaints raised by this tenant
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Complaint> complaints = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "registered_at", updatable = false)
    private LocalDateTime registeredAt;
}
