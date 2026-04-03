package com.pg.controller;
import com.pg.dto.request.ComplaintRequest;
import com.pg.dto.response.ComplaintResponse;
import com.pg.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;
    private String getEmail() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
    @PostMapping
    public ResponseEntity<ComplaintResponse> raise(@Valid @RequestBody ComplaintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(complaintService.raiseComplaint(getEmail(), request));
    }
    @GetMapping("/my")
    public ResponseEntity<List<ComplaintResponse>> getMyComplaints() { return ResponseEntity.ok(complaintService.getMyComplaints(getEmail())); }
    @PutMapping("/{id}/close")
    public ResponseEntity<ComplaintResponse> close(@PathVariable Long id) { return ResponseEntity.ok(complaintService.closeComplaint(getEmail(), id)); }
    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAll() { return ResponseEntity.ok(complaintService.getAllComplaints(getEmail())); }
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ComplaintResponse> resolve(@PathVariable Long id, @RequestParam(required = false) String response) {
        return ResponseEntity.ok(complaintService.resolveComplaint(getEmail(), id, response));
    }
}
