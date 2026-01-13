package com.hotel.mcp.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Statistics for hotel revenue over a period.
 */
public record RevenueStats(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalRevenue,
        long numberOfBookings,
        BigDecimal averageDailyRate) {
    public static RevenueStats of(LocalDate start, LocalDate end, BigDecimal totalRevenue, long count) {
        BigDecimal adr = count > 0 ? totalRevenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return new RevenueStats(start, end, totalRevenue, count, adr);
    }
}
