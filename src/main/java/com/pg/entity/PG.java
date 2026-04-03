package com.pg.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pgs", indexes = {
        @Index(name = "idx_pg_city", columnList = "city"),
        @Index(name = "idx_pg_owner", columnList = "owner_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PG {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 1000)
    private String description;

    @Column(name = "contact_phone", length = 15)
    private String contactPhone;

    @Column(name = "base_monthly_rent", precision = 10, scale = 2)
    private BigDecimal baseMonthlyRent;

    @Column(name = "rent_due_day")
    @Builder.Default
    private Integer rentDueDay = 5;

    @Column(name = "penalty_percent")
    @Builder.Default
    private Integer penaltyPercent = 5;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
