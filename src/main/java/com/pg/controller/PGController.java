package com.pg.controller;
import com.pg.dto.request.PGRequest;
import com.pg.dto.response.PGResponse;
import com.pg.service.PGService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/pg")
@RequiredArgsConstructor
public class PGController {
    private final PGService pgService;
    private String getEmail() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
    @PostMapping
    public ResponseEntity<PGResponse> registerPg(@Valid @RequestBody PGRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pgService.registerPg(getEmail(), request));
    }
    @GetMapping("/my")
    public ResponseEntity<PGResponse> getMyPg() { return ResponseEntity.ok(pgService.getMyPg(getEmail())); }
    @PutMapping("/my")
    public ResponseEntity<PGResponse> updateMyPg(@Valid @RequestBody PGRequest request) {
        return ResponseEntity.ok(pgService.updateMyPg(getEmail(), request));
    }
    @GetMapping
    public ResponseEntity<List<PGResponse>> getAllPgs() { return ResponseEntity.ok(pgService.getAllPgs()); }
}
