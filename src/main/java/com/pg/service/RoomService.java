package com.pg.service;

import com.pg.dto.request.RoomRequest;
import com.pg.dto.response.RoomResponse;
import com.pg.entity.PG;
import com.pg.entity.Room;
import com.pg.entity.enums.RoomStatus;
import com.pg.exception.ResourceNotFoundException;
import com.pg.exception.UnauthorizedAccessException;
import com.pg.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final PGService pgService;

    @Transactional
    public RoomResponse addRoom(String ownerEmail, RoomRequest request) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        if (roomRepository.findByPgIdAndRoomNumber(pg.getId(), request.getRoomNumber()).isPresent()) {
            throw new RuntimeException("Room " + request.getRoomNumber() + " already exists.");
        }

        BigDecimal rent = request.getMonthlyRent() != null
                ? request.getMonthlyRent() : pg.getBaseMonthlyRent();

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .capacity(request.getCapacity())
                .monthlyRent(rent)
                .floorNumber(request.getFloorNumber())
                .amenities(request.getAmenities())
                .status(RoomStatus.AVAILABLE)
                .pg(pg)
                .build();

        return toResponse(roomRepository.save(room));
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(String ownerEmail) {
        PG pg = pgService.getOwnerPg(ownerEmail);
        return roomRepository.findByPgId(pg.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAvailableRooms(String ownerEmail) {
        PG pg = pgService.getOwnerPg(ownerEmail);
        return roomRepository.findAvailableRoomsInPg(pg.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse updateRoom(String ownerEmail, Long roomId, RoomRequest request) {
        PG pg = pgService.getOwnerPg(ownerEmail);

        if (!roomRepository.existsByIdAndOwnerId(roomId, pg.getOwner().getId())) {
            throw new UnauthorizedAccessException("Room does not belong to your PG.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        if (request.getAmenities() != null)   room.setAmenities(request.getAmenities());
        if (request.getMonthlyRent() != null) room.setMonthlyRent(request.getMonthlyRent());
        if (request.getFloorNumber() != null) room.setFloorNumber(request.getFloorNumber());

        return toResponse(roomRepository.save(room));
    }

    private RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId()).roomNumber(room.getRoomNumber())
                .type(room.getType()).capacity(room.getCapacity())
                .currentOccupancy(room.getCurrentOccupancy())
                .availableBeds(room.getCapacity() - room.getCurrentOccupancy())
                .monthlyRent(room.getMonthlyRent()).floorNumber(room.getFloorNumber())
                .amenities(room.getAmenities()).status(room.getStatus())
                .pgId(room.getPg().getId()).pgName(room.getPg().getName())
                .build();
    }
}
