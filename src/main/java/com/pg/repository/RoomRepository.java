package com.pg.repository;

import com.pg.entity.Room;
import com.pg.entity.enums.RoomStatus;
import com.pg.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByPgId(Long pgId);

    List<Room> findByPgIdAndStatus(Long pgId, RoomStatus status);

    Optional<Room> findByPgIdAndRoomNumber(Long pgId, String roomNumber);

    long countByPgIdAndStatus(Long pgId, RoomStatus status);

    @Query("SELECT COUNT(r) > 0 FROM Room r WHERE r.id = :roomId AND r.pg.owner.id = :ownerId")
    boolean existsByIdAndOwnerId(@Param("roomId") Long roomId, @Param("ownerId") Long ownerId);

    @Query("SELECT r FROM Room r WHERE r.pg.id = :pgId " +
           "AND r.status = 'AVAILABLE' " +
           "AND r.currentOccupancy < r.capacity " +
           "ORDER BY r.currentOccupancy DESC")
    List<Room> findAvailableRoomsInPg(@Param("pgId") Long pgId);

    List<Room> findByPgIdAndType(Long pgId, RoomType type);
}
