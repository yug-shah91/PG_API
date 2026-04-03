package com.pg.controller;
import com.pg.dto.request.RoomRequest;
import com.pg.dto.response.RoomResponse;
import com.pg.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private String getEmail() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
    @PostMapping
    public ResponseEntity<RoomResponse> addRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addRoom(getEmail(), request));
    }
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() { return ResponseEntity.ok(roomService.getAllRooms(getEmail())); }
    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() { return ResponseEntity.ok(roomService.getAvailableRooms(getEmail())); }
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(getEmail(), roomId, request));
    }
}
