package com.hotel.mcp.dto;

import java.math.BigDecimal;

/**
 * DTO for booking creation result.
 */
public record BookingResult(
        boolean success,
        String bookingReference,
        String roomNumber,
        String roomType,
        String guestName,
        String checkInDate,
        String checkOutDate,
        int numberOfNights,
        BigDecimal pricePerNight,
        BigDecimal totalPrice,
        String status,
        String message) {
    public static BookingResult success(String bookingReference, String roomNumber,
            String roomType, String guestName,
            String checkInDate, String checkOutDate,
            int numberOfNights, BigDecimal pricePerNight,
            BigDecimal totalPrice) {
        return new BookingResult(
                true,
                bookingReference,
                roomNumber,
                roomType,
                guestName,
                checkInDate,
                checkOutDate,
                numberOfNights,
                pricePerNight,
                totalPrice,
                "PENDING",
                "Booking created successfully! Please confirm your booking using reference: " + bookingReference);
    }

    public static BookingResult failure(String message) {
        return new BookingResult(
                false, null, null, null, null, null, null, 0, null, null, null, message);
    }
}
