package com.hotel.mcp.dto;

import java.util.List;

/**
 * DTO for availability check result.
 */
public record AvailabilityResult(
        boolean available,
        String checkInDate,
        String checkOutDate,
        String roomType,
        int numberOfNights,
        int availableRoomCount,
        List<AvailableRoom> availableRooms,
        String message) {
    public static AvailabilityResult success(String checkInDate, String checkOutDate,
            String roomType, int numberOfNights,
            List<AvailableRoom> rooms) {
        return new AvailabilityResult(
                true,
                checkInDate,
                checkOutDate,
                roomType,
                numberOfNights,
                rooms.size(),
                rooms,
                rooms.size() + " room(s) available for your selected dates.");
    }

    public static AvailabilityResult noAvailability(String checkInDate, String checkOutDate,
            String roomType, int numberOfNights) {
        return new AvailabilityResult(
                false,
                checkInDate,
                checkOutDate,
                roomType,
                numberOfNights,
                0,
                List.of(),
                "Sorry, no rooms of type " + roomType + " are available for the selected dates.");
    }

    public static AvailabilityResult error(String message) {
        return new AvailabilityResult(
                false, null, null, null, 0, 0, List.of(), message);
    }
}
