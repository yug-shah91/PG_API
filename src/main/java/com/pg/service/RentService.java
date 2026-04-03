package com.pg.service;

import com.pg.dto.request.RentPaymentRequest;
import com.pg.dto.response.RentRecordResponse;
import com.pg.entity.*;
import com.pg.entity.enums.RentStatus;
import com.pg.exception.ResourceNotFoundException;
import com.pg.exception.UnauthorizedAccessException;
import com.pg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRecordRepository rentRecordRepository;
    private final TenantRepository tenantRepository;
    private final PGService pgService;

    @Transactional
    public RentRecordResponse createRentRecord(String ownerEmail, Long tenantId, Integer month, Integer year) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        if (!tenantRepository.existsByIdAndPgId(tenantId, pg.getId())) {
            throw new UnauthorizedAccessException("Tenant does not belong to your PG.");
        }

        if (rentRecordRepository.existsByTenantIdAndMonthAndYear(tenantId, month, year)) {
            throw new RuntimeException("Rent record already exists for this tenant for " + month + "/" + year);
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        BigDecimal amount = pg.getBaseMonthlyRent();

        RentRecord record = new RentRecord();
        record.setTenant(tenant);
        record.setPg(pg);
        record.setMonth(month);
        record.setYear(year);
        record.setBaseAmount(amount);
        record.setPenaltyAmount(BigDecimal.ZERO);
        record.setTotalAmount(amount);
        record.setDueDate(LocalDate.of(year, month, pg.getRentDueDay()));
        record.setStatus(RentStatus.PENDING);

        return toResponse(rentRecordRepository.save(record));
    }

    @Transactional
    public RentRecordResponse markAsPaid(String ownerEmail, Long rentRecordId, RentPaymentRequest request) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        RentRecord record = rentRecordRepository.findById(rentRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Rent record not found"));

        if (!record.getPg().getId().equals(pg.getId())) {
            throw new UnauthorizedAccessException("Rent record does not belong to your PG.");
        }

        if (record.getStatus() == RentStatus.PAID) {
            throw new RuntimeException("Rent is already marked as paid.");
        }

        record.setStatus(RentStatus.PAID);
        record.setPaidAt(LocalDateTime.now());
        if (request != null && request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }

        return toResponse(rentRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<RentRecordResponse> getPendingRent(String ownerEmail) {
        PG pg = pgService.getOwnerPg(ownerEmail);
        return rentRecordRepository.findByPgIdAndStatusOrderByDueDateAsc(pg.getId(), RentStatus.PENDING)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentRecordResponse> getMonthlyReport(String ownerEmail, int month, int year) {
        PG pg = pgService.getOwnerPg(ownerEmail);
        return rentRecordRepository.findByPgIdAndMonthAndYear(pg.getId(), month, year)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentRecordResponse> getMyRentHistory(String tenantEmail) {
        Tenant tenant = tenantRepository.findByUserEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile not found"));
        return rentRecordRepository.findByTenantIdOrderByYearDescMonthDesc(tenant.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private RentRecordResponse toResponse(RentRecord r) {
        String roomNumber = "N/A";
        try {
            if (r.getTenant().getRoom() != null) {
                roomNumber = r.getTenant().getRoom().getRoomNumber();
            }
        } catch (Exception e) {
            roomNumber = "N/A";
        }
        return RentRecordResponse.builder()
                .id(r.getId()).tenantId(r.getTenant().getId())
                .tenantName(r.getTenant().getUser().getName())
                .roomNumber(roomNumber).month(r.getMonth()).year(r.getYear())
                .baseAmount(r.getBaseAmount()).penaltyAmount(r.getPenaltyAmount())
                .totalAmount(r.getTotalAmount()).dueDate(r.getDueDate())
                .status(r.getStatus()).paidAt(r.getPaidAt()).notes(r.getNotes())
                .createdAt(r.getCreatedAt()).build();
    }
}
