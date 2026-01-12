package com.hotel.mcp.service;

import com.hotel.mcp.dto.AvailabilityResult;
import com.hotel.mcp.dto.AvailableRoom;
import com.hotel.mcp.dto.RoomTypeInfo;
import com.hotel.mcp.entity.Room;
import com.hotel.mcp.entity.RoomType;
import com.hotel.mcp.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for room-related operations.
 */
@Service
@Transactional(readOnly = true)
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Get information about all room types with pricing and availability.
     */
    public List<RoomTypeInfo> getAllRoomTypes() {
        log.info("Fetching all room types");

        List<Room> allRooms = roomRepository.findByAvailableTrue();

        // Group by room type and calculate aggregates
        Map<RoomType, List<Room>> roomsByType = allRooms.stream()
                .collect(Collectors.groupingBy(Room::getType));

        List<RoomTypeInfo> result = new ArrayList<>();

        for (RoomType type : RoomType.values()) {
            List<Room> rooms = roomsByType.getOrDefault(type, Collections.emptyList());

            BigDecimal startingPrice = rooms.stream()
                    .map(Room::getPricePerNight)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            int maxOccupancy = rooms.stream()
                    .mapToInt(Room::getMaxOccupancy)
                    .max()
                    .orElse(0);

            result.add(RoomTypeInfo.from(type, startingPrice, maxOccupancy, rooms.size()));
        }

        return result;
    }

    /**
     * Check room availability for specific dates and room type.
     */
    public AvailabilityResult checkAvailability(String roomTypeStr, String checkInStr, String checkOutStr) {
        log.info("Checking availability for type={}, checkIn={}, checkOut={}",
                roomTypeStr, checkInStr, checkOutStr);

        // Parse and validate room type
        RoomType roomType;
        try {
            roomType = RoomType.valueOf(roomTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AvailabilityResult.error("Invalid room type: " + roomTypeStr +
                    ". Valid types are: SINGLE, DOUBLE, SUITE, DELUXE");
        }

        // Parse and validate dates
        LocalDate checkIn, checkOut;
        try {
            checkIn = LocalDate.parse(checkInStr);
            checkOut = LocalDate.parse(checkOutStr);
        } catch (DateTimeParseException e) {
            return AvailabilityResult.error("Invalid date format. Please use YYYY-MM-DD format.");
        }

        // Validate date logic
        if (checkIn.isBefore(LocalDate.now())) {
            return AvailabilityResult.error("Check-in date cannot be in the past.");
        }
        if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
            return AvailabilityResult.error("Check-out date must be after check-in date.");
        }

        int numberOfNights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);

        // Find available rooms
        List<Room> availableRooms = roomRepository.findAvailableRoomsByTypeAndDateRange(
                roomType, checkIn, checkOut);

        if (availableRooms.isEmpty()) {
            return AvailabilityResult.noAvailability(checkInStr, checkOutStr,
                    roomType.getDisplayName(), numberOfNights);
        }

        List<AvailableRoom> roomDtos = availableRooms.stream()
                .map(room -> new AvailableRoom(
                        room.getRoomNumber(),
                        room.getType().getDisplayName(),
                        room.getDescription(),
                        room.getAmenities(),
                        room.getPricePerNight(),
                        room.getMaxOccupancy()))
                .toList();

        return AvailabilityResult.success(checkInStr, checkOutStr,
                roomType.getDisplayName(), numberOfNights, roomDtos);
    }

    /**
     * Find a room by room number.
     */
    public Optional<Room> findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
    }

    /**
     * Check if a specific room is available for dates.
     */
    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.isRoomAvailableForDates(roomId, checkIn, checkOut);
    }
}
