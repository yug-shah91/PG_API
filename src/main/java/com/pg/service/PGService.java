package com.pg.service;

import com.pg.dto.request.PGRequest;
import com.pg.dto.response.PGResponse;
import com.pg.entity.PG;
import com.pg.entity.User;
import com.pg.entity.enums.RoomStatus;
import com.pg.exception.ResourceNotFoundException;
import com.pg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PGService {

    private final PGRepository pgRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    // Helper used by all methods — gets logged-in owner's PG
    PG getOwnerPg(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return pgRepository.findByOwnerIdAndIsActiveTrue(owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No PG found. Register your PG first."));
    }

    @Transactional
    public PGResponse registerPg(String ownerEmail, PGRequest request) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        if (pgRepository.existsByOwnerId(owner.getId())) {
            throw new RuntimeException("You already have a PG registered.");
        }
        PG pg = PG.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .description(request.getDescription())
                .contactPhone(request.getContactPhone())
                .baseMonthlyRent(request.getBaseMonthlyRent())
                .rentDueDay(request.getRentDueDay() != null ? request.getRentDueDay() : 5)
                .penaltyPercent(request.getPenaltyPercent() != null ? request.getPenaltyPercent() : 5)
                .owner(owner)
                .build();
        return toResponse(pgRepository.save(pg));
    }

    @Transactional(readOnly = true)
    public PGResponse getMyPg(String ownerEmail) {
        return toResponse(getOwnerPg(ownerEmail));
    }

    @Transactional
    public PGResponse updateMyPg(String ownerEmail, PGRequest request) {
        PG pg = getOwnerPg(ownerEmail);
        if (request.getName() != null) pg.setName(request.getName());
        if (request.getAddress() != null) pg.setAddress(request.getAddress());
        if (request.getCity() != null)pg.setCity(request.getCity());
        if (request.getDescription() != null)pg.setDescription(request.getDescription());
        if (request.getContactPhone() != null)pg.setContactPhone(request.getContactPhone());
        if (request.getBaseMonthlyRent() != null)pg.setBaseMonthlyRent(request.getBaseMonthlyRent());
        return toResponse(pgRepository.save(pg));
    }

    @Transactional(readOnly = true)
    public List<PGResponse> getAllPgs() {
        return pgRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private PGResponse toResponse(PG pg) {
        long availableRooms = roomRepository.countByPgIdAndStatus(pg.getId(), RoomStatus.AVAILABLE);
        long activeTenants  = tenantRepository.countByPgIdAndIsActiveTrue(pg.getId());
        return PGResponse.builder()
                .id(pg.getId()).name(pg.getName()).address(pg.getAddress())
                .city(pg.getCity()).description(pg.getDescription())
                .contactPhone(pg.getContactPhone()).baseMonthlyRent(pg.getBaseMonthlyRent())
                .rentDueDay(pg.getRentDueDay()).penaltyPercent(pg.getPenaltyPercent())
                .isActive(pg.getIsActive()).ownerName(pg.getOwner().getName())
                .ownerEmail(pg.getOwner().getEmail()).totalRooms(pg.getRooms().size())
                .availableRooms((int) availableRooms).activeTenants(activeTenants)
                .createdAt(pg.getCreatedAt()).build();
    }
}
