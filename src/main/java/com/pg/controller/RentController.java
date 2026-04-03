package com.pg.controller;
import com.pg.dto.request.RentPaymentRequest;
import com.pg.dto.response.RentRecordResponse;
import com.pg.service.RentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/rent")
@RequiredArgsConstructor
public class RentController {
    private final RentService rentService;
    private String getEmail() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
    @PostMapping("/create")
    public ResponseEntity<RentRecordResponse> createRent(@RequestParam Long tenantId, @RequestParam Integer month, @RequestParam Integer year) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentService.createRentRecord(getEmail(), tenantId, month, year));
    }
    @PutMapping("/{rentRecordId}/mark-paid")
    public ResponseEntity<RentRecordResponse> markPaid(@PathVariable Long rentRecordId, @RequestBody(required = false) RentPaymentRequest request) {
        return ResponseEntity.ok(rentService.markAsPaid(getEmail(), rentRecordId, request));
    }
    @GetMapping("/pending")
    public ResponseEntity<List<RentRecordResponse>> getPending() { return ResponseEntity.ok(rentService.getPendingRent(getEmail())); }
    @GetMapping("/report")
    public ResponseEntity<List<RentRecordResponse>> getReport(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(rentService.getMonthlyReport(getEmail(), month, year));
    }
    @GetMapping("/my")
    public ResponseEntity<List<RentRecordResponse>> getMyRent() { return ResponseEntity.ok(rentService.getMyRentHistory(getEmail())); }
}
