package com.hotel.mcp.dto;

import java.math.BigDecimal;

/**
 * DTO for available room information.
 */
public record AvailableRoom(
        String roomNumber,
        String type,
        String description,
        String amenities,
        BigDecimal pricePerNight,
        int maxOccupancy) {
}
