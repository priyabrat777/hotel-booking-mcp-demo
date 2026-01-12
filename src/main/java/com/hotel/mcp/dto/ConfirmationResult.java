package com.hotel.mcp.dto;

/**
 * DTO for booking confirmation result.
 */
public record ConfirmationResult(
        boolean success,
        String bookingReference,
        String status,
        String guestName,
        String roomNumber,
        String checkInDate,
        String checkOutDate,
        String message) {
    public static ConfirmationResult success(String bookingReference, String guestName,
            String roomNumber, String checkInDate,
            String checkOutDate) {
        return new ConfirmationResult(
                true,
                bookingReference,
                "CONFIRMED",
                guestName,
                roomNumber,
                checkInDate,
                checkOutDate,
                "Your booking has been confirmed! We look forward to welcoming you.");
    }

    public static ConfirmationResult failure(String bookingReference, String message) {
        return new ConfirmationResult(
                false, bookingReference, null, null, null, null, null, message);
    }

    public static ConfirmationResult notFound(String bookingReference) {
        return new ConfirmationResult(
                false, bookingReference, null, null, null, null, null,
                "Booking with reference '" + bookingReference + "' was not found.");
    }

    public static ConfirmationResult alreadyConfirmed(String bookingReference) {
        return new ConfirmationResult(
                false, bookingReference, "CONFIRMED", null, null, null, null,
                "Booking '" + bookingReference + "' is already confirmed.");
    }
}
