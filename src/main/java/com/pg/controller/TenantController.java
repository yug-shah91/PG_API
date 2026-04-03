package com.pg.controller;
import com.pg.dto.request.TenantRequest;
import com.pg.dto.response.TenantResponse;
import com.pg.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;
    private String getEmail() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
    @PostMapping
    public ResponseEntity<TenantResponse> registerTenant(@Valid @RequestBody TenantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.registerTenant(getEmail(), request));
    }
    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() { return ResponseEntity.ok(tenantService.getAllTenants(getEmail())); }
    @PutMapping("/{tenantId}/room/{roomId}")
    public ResponseEntity<TenantResponse> allocateRoom(@PathVariable Long tenantId, @PathVariable Long roomId) {
        return ResponseEntity.ok(tenantService.allocateRoom(getEmail(), tenantId, roomId));
    }
    @PutMapping("/{tenantId}/checkout")
    public ResponseEntity<TenantResponse> checkout(@PathVariable Long tenantId) {
        return ResponseEntity.ok(tenantService.checkoutTenant(getEmail(), tenantId));
    }
    @GetMapping("/my")
    public ResponseEntity<TenantResponse> getMyProfile() { return ResponseEntity.ok(tenantService.getMyProfile(getEmail())); }
}
