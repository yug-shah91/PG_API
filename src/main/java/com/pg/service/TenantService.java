package com.pg.service;

import com.pg.dto.request.TenantRequest;
import com.pg.dto.response.TenantResponse;
import com.pg.entity.*;
import com.pg.entity.enums.Role;
import com.pg.entity.enums.RoomStatus;
import com.pg.exception.ResourceNotFoundException;
import com.pg.exception.UnauthorizedAccessException;
import com.pg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private final PGService pgService;

    @Transactional
    public TenantResponse registerTenant(String ownerEmail, TenantRequest request) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Create User account for tenant
        User user = User.builder()
                .name(request.getName()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone()).role(Role.TENANT).build();
        userRepository.save(user);

        // Create Tenant profile
        Tenant tenant = Tenant.builder()
                .user(user).pg(pg)
                .joinDate(request.getJoinDate() != null ? request.getJoinDate() : LocalDate.now())
                .expectedLeaveDate(request.getExpectedLeaveDate())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .idProofType(request.getIdProofType())
                .idProofNumber(request.getIdProofNumber())
                .build();

        // Assign room if provided
        if (request.getRoomId() != null) {
            assignRoom(tenant, request.getRoomId(), pg);
        }

        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional
    public TenantResponse allocateRoom(String ownerEmail, Long tenantId, Long roomId) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        if (!tenantRepository.existsByIdAndPgId(tenantId, pg.getId())) {
            throw new UnauthorizedAccessException("Tenant does not belong to your PG.");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (tenant.getRoom() != null) {
            freeRoom(tenant.getRoom());
        }

        assignRoom(tenant, roomId, pg);
        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional
    public TenantResponse checkoutTenant(String ownerEmail, Long tenantId) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        if (!tenantRepository.existsByIdAndPgId(tenantId, pg.getId())) {
            throw new UnauthorizedAccessException("Tenant does not belong to your PG.");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (tenant.getRoom() != null) {
            freeRoom(tenant.getRoom());
            tenant.setRoom(null);
        }

        tenant.setIsActive(false);
        tenant.getUser().setIsActive(false);
        userRepository.save(tenant.getUser());

        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants(String ownerEmail) {
        PG pg = pgService.getOwnerPg(ownerEmail);
        return tenantRepository.findByPgIdAndIsActiveTrue(pg.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TenantResponse getMyProfile(String tenantEmail) {
        Tenant tenant = tenantRepository.findByUserEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile not found"));
        return toResponse(tenant);
    }

    private void assignRoom(Tenant tenant, Long roomId, PG pg) {
        if (!roomRepository.existsByIdAndOwnerId(roomId, pg.getOwner().getId())) {
            throw new UnauthorizedAccessException("Room does not belong to your PG.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getStatus() == RoomStatus.UNDER_MAINTENANCE) {
            throw new RuntimeException("Room is under maintenance.");
        }
        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            throw new RuntimeException("Room is fully occupied.");
        }

        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
        room.setStatus(room.getCurrentOccupancy().equals(room.getCapacity())
                ? RoomStatus.FULLY_OCCUPIED : RoomStatus.AVAILABLE);
        roomRepository.save(room);
        tenant.setRoom(room);
    }

    private void freeRoom(Room room) {
        room.setCurrentOccupancy(Math.max(0, room.getCurrentOccupancy() - 1));
        if (room.getStatus() != RoomStatus.UNDER_MAINTENANCE) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);
    }

    private TenantResponse toResponse(Tenant t) {
        return TenantResponse.builder()
                .id(t.getId()).name(t.getUser().getName()).email(t.getUser().getEmail())
                .phone(t.getUser().getPhone())
                .roomId(t.getRoom() != null ? t.getRoom().getId() : null)
                .roomNumber(t.getRoom() != null ? t.getRoom().getRoomNumber() : null)
                .pgId(t.getPg().getId()).pgName(t.getPg().getName())
                .joinDate(t.getJoinDate()).expectedLeaveDate(t.getExpectedLeaveDate())
                .emergencyContactName(t.getEmergencyContactName())
                .emergencyContactPhone(t.getEmergencyContactPhone())
                .idProofType(t.getIdProofType()).isActive(t.getIsActive())
                .registeredAt(t.getRegisteredAt()).build();
    }
}
