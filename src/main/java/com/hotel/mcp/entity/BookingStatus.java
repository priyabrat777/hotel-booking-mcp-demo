package com.hotel.mcp.entity;

/**
 * Enum representing the status of a booking.
 */
public enum BookingStatus {
    PENDING("Pending", "Booking is awaiting confirmation"),
    CONFIRMED("Confirmed", "Booking has been confirmed"),
    CANCELLED("Cancelled", "Booking has been cancelled"),
    COMPLETED("Completed", "Guest has checked out");

    private final String displayName;
    private final String description;

    BookingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
