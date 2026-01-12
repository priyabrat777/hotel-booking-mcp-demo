package com.hotel.mcp.dto;

import com.hotel.mcp.entity.RoomType;
import java.math.BigDecimal;

/**
 * DTO for room type information.
 */
public record RoomTypeInfo(
        String type,
        String displayName,
        String description,
        BigDecimal startingPrice,
        int maxOccupancy,
        int availableRooms) {
    public static RoomTypeInfo from(RoomType type, BigDecimal startingPrice,
            int maxOccupancy, int availableRooms) {
        return new RoomTypeInfo(
                type.name(),
                type.getDisplayName(),
                type.getDescription(),
                startingPrice,
                maxOccupancy,
                availableRooms);
    }
}
