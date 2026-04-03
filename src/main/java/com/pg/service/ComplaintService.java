package com.pg.service;

import com.pg.dto.request.ComplaintRequest;
import com.pg.dto.response.ComplaintResponse;
import com.pg.entity.*;
import com.pg.entity.enums.ComplaintStatus;
import com.pg.exception.ResourceNotFoundException;
import com.pg.exception.UnauthorizedAccessException;
import com.pg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final TenantRepository tenantRepository;
    private final PGService pgService;

    @Transactional
    public ComplaintResponse raiseComplaint(String tenantEmail, ComplaintRequest request) {
        Tenant tenant = tenantRepository.findByUserEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Complaint complaint = Complaint.builder()
                .tenant(tenant).pg(tenant.getPg())
                .category(request.getCategory())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(ComplaintStatus.OPEN)
                .build();

        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getMyComplaints(String tenantEmail) {
        Tenant tenant = tenantRepository.findByUserEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        return complaintRepository.findByTenantIdOrderByCreatedAtDesc(tenant.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAllComplaints(String ownerEmail) {
        PG pg = pgService.getOwnerPg(ownerEmail);
        return complaintRepository.findByPgIdOrderByCreatedAtDesc(
                        pg.getId(), org.springframework.data.domain.Pageable.unpaged())
                .getContent()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ComplaintResponse resolveComplaint(String ownerEmail, Long complaintId, String response) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        if (!complaintRepository.existsByIdAndPgId(complaintId, pg.getId())) {
            throw new UnauthorizedAccessException("Complaint does not belong to your PG.");
        }

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setOwnerResponse(response);
        complaint.setResolvedAt(LocalDateTime.now());

        return toResponse(complaintRepository.save(complaint));
    }

    @Transactional
    public ComplaintResponse closeComplaint(String tenantEmail, Long complaintId) {
        Tenant tenant = tenantRepository.findByUserEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (!complaintRepository.existsByIdAndTenantId(complaintId, tenant.getId())) {
            throw new UnauthorizedAccessException("Complaint does not belong to you.");
        }

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.CLOSED);
        return toResponse(complaintRepository.save(complaint));
    }

    private ComplaintResponse toResponse(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId()).tenantId(c.getTenant().getId())
                .tenantName(c.getTenant().getUser().getName())
                .roomNumber(c.getTenant().getRoom() != null
                        ? c.getTenant().getRoom().getRoomNumber() : "N/A")
                .pgId(c.getPg().getId()).category(c.getCategory())
                .title(c.getTitle()).description(c.getDescription())
                .status(c.getStatus()).ownerResponse(c.getOwnerResponse())
                .resolvedAt(c.getResolvedAt()).createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt()).build();
    }
}
