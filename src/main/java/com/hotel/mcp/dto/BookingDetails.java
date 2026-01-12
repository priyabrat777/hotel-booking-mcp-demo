package com.hotel.mcp.dto;

import com.hotel.mcp.entity.Booking;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * DTO for complete booking details.
 */
public record BookingDetails(
        boolean found,
        String bookingReference,
        String status,
        String guestName,
        String guestEmail,
        String guestPhone,
        String roomNumber,
        String roomType,
        String checkInDate,
        String checkOutDate,
        int numberOfNights,
        BigDecimal pricePerNight,
        BigDecimal totalPrice,
        String createdAt,
        String message) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static BookingDetails from(Booking booking) {
        int nights = (int) (booking.getCheckOutDate().toEpochDay() - booking.getCheckInDate().toEpochDay());
        return new BookingDetails(
                true,
                booking.getBookingReference(),
                booking.getStatus().getDisplayName(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getGuestPhone(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getType().getDisplayName(),
                booking.getCheckInDate().format(DATE_FORMATTER),
                booking.getCheckOutDate().format(DATE_FORMATTER),
                nights,
                booking.getRoom().getPricePerNight(),
                booking.getTotalPrice(),
                booking.getCreatedAt().format(DATETIME_FORMATTER),
                "Booking found.");
    }

    public static BookingDetails notFound(String bookingReference) {
        return new BookingDetails(
                false, bookingReference, null, null, null, null, null, null,
                null, null, 0, null, null, null,
                "Booking with reference '" + bookingReference + "' was not found.");
    }
}
