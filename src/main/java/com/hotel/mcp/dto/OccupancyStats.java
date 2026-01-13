package com.hotel.mcp.dto;

import java.time.LocalDate;

/**
 * Statistics for hotel occupancy on a specific date.
 */
public record OccupancyStats(
        LocalDate date,
        long totalRooms,
        long occupiedRooms,
        double occupancyRate,
        long expectedCheckIns,
        long expectedCheckOuts) {
    public static OccupancyStats of(LocalDate date, long totalRooms, long occupiedRooms, long checkIns,
            long checkOuts) {
        double rate = totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100 : 0.0;
        return new OccupancyStats(date, totalRooms, occupiedRooms, Math.round(rate * 100.0) / 100.0, checkIns,
                checkOuts);
    }
}
