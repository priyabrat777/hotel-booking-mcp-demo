package com.hotel.mcp.repository;

import com.hotel.mcp.entity.Room;
import com.hotel.mcp.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Room entity operations.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Find a room by its room number.
     */
    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * Find all rooms of a specific type.
     */
    List<Room> findByType(RoomType type);

    /**
     * Find all available rooms of a specific type.
     */
    List<Room> findByTypeAndAvailableTrue(RoomType type);

    /**
     * Find all available rooms.
     */
    List<Room> findByAvailableTrue();

    /**
     * Find rooms that are available for the given date range.
     * A room is available if there are no confirmed or pending bookings
     * that overlap with the requested dates.
     */
    @Query("""
            SELECT r FROM Room r
            WHERE r.type = :roomType
            AND r.available = true
            AND r.id NOT IN (
                SELECT b.room.id FROM Booking b
                WHERE b.status IN ('PENDING', 'CONFIRMED')
                AND (
                    (b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)
                )
            )
            """)
    List<Room> findAvailableRoomsByTypeAndDateRange(
            @Param("roomType") RoomType roomType,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    /**
     * Find all available rooms for a date range (any type).
     */
    @Query("""
            SELECT r FROM Room r
            WHERE r.available = true
            AND r.id NOT IN (
                SELECT b.room.id FROM Booking b
                WHERE b.status IN ('PENDING', 'CONFIRMED')
                AND (
                    (b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)
                )
            )
            """)
    List<Room> findAvailableRoomsForDateRange(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    /**
     * Check if a specific room is available for the given dates.
     */
    @Query("""
            SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END
            FROM Booking b
            WHERE b.room.id = :roomId
            AND b.status IN ('PENDING', 'CONFIRMED')
            AND (b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)
            """)
    boolean isRoomAvailableForDates(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}
