package com.hotel.mcp.dto;

/**
 * DTO for booking cancellation result.
 */
public record CancellationResult(
        boolean success,
        String bookingReference,
        String previousStatus,
        String currentStatus,
        String message) {
    public static CancellationResult success(String bookingReference, String previousStatus) {
        return new CancellationResult(
                true,
                bookingReference,
                previousStatus,
                "CANCELLED",
                "Booking '" + bookingReference + "' has been successfully cancelled.");
    }

    public static CancellationResult failure(String bookingReference, String message) {
        return new CancellationResult(
                false, bookingReference, null, null, message);
    }

    public static CancellationResult notFound(String bookingReference) {
        return new CancellationResult(
                false, bookingReference, null, null,
                "Booking with reference '" + bookingReference + "' was not found.");
    }

    public static CancellationResult alreadyCancelled(String bookingReference) {
        return new CancellationResult(
                false, bookingReference, "CANCELLED", "CANCELLED",
                "Booking '" + bookingReference + "' is already cancelled.");
    }
}
