package com.hotel.mcp.repository;

import com.hotel.mcp.entity.Booking;
import com.hotel.mcp.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking entity operations.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Find a booking by its reference code.
     */
    Optional<Booking> findByBookingReference(String bookingReference);

    /**
     * Find all bookings for a guest by email.
     */
    List<Booking> findByGuestEmail(String guestEmail);

    /**
     * Find all bookings with a specific status.
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Find all bookings for a room.
     */
    List<Booking> findByRoomId(Long roomId);

    /**
     * Find bookings for a room that overlap with specified dates.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.room.id = :roomId
            AND b.status IN ('PENDING', 'CONFIRMED')
            AND (b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)
            """)
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    /**
     * Find all bookings for a specific date range.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.checkInDate >= :startDate
            AND b.checkOutDate <= :endDate
            ORDER BY b.checkInDate
            """)
    List<Booking> findBookingsInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find active bookings (pending or confirmed).
     */
    @Query("SELECT b FROM Booking b WHERE b.status IN ('PENDING', 'CONFIRMED') ORDER BY b.checkInDate")
    List<Booking> findActiveBookings();

    /**
     * Count bookings by status.
     */
    long countByStatus(BookingStatus status);
}
