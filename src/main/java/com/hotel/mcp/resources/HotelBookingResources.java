package com.hotel.mcp.resources;

import com.hotel.mcp.entity.Room;
import com.hotel.mcp.service.RoomService;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * MCP Resources for Hotel Booking.
 * Provides structured information about the hotel and its rooms.
 */
@Component
public class HotelBookingResources {

    private final RoomService roomService;

    public HotelBookingResources(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * General hotel information and policies.
     */
    @McpResource(uri = "hotel://info", name = "Hotel Information", description = "General information about the hotel, check-in/out times, and policies.")
    public ReadResourceResult getHotelInfo() {
        String info = """
                # Grand Plaza Hotel Information

                **Location:** 123 Luxury Lane, Bangalore, India
                **Check-in Time:** 2:00 PM
                **Check-out Time:** 11:00 AM

                ## Policies
                - Cancellation: Free cancellation up to 24 hours before check-in.
                - Smoking: All rooms are non-smoking.
                - Pets: Only service animals allowed.
                - Parking: Complimentary valet parking for guests.

                ## Amenities
                - Rooftop Swimming Pool
                - 24/7 Fitness Center
                - Multi-cuisine Restaurant
                - High-speed WiFi in all areas
                """;

        return new ReadResourceResult(List.of(
                new TextResourceContents("hotel://info", "text/markdown", info)));
    }

    /**
     * List of all rooms in the hotel.
     */
    @McpResource(uri = "hotel://rooms", name = "Room List", description = "A list of all room numbers and their types.")
    public ReadResourceResult listRooms() {
        List<com.hotel.mcp.dto.RoomTypeInfo> roomTypes = roomService.getAllRoomTypes();

        StringBuilder list = new StringBuilder("# Hotel Rooms\n\n");
        for (var type : roomTypes) {
            list.append("## ").append(type.displayName()).append("\n");
            list.append("- **Category:** ").append(type.type()).append("\n");
            list.append("- **Description:** ").append(type.description()).append("\n");
            list.append("- **Starting Price:** INR ").append(type.startingPrice()).append("\n");
            list.append("- **Available Rooms:** ").append(type.availableRooms()).append("\n\n");
        }

        return new ReadResourceResult(List.of(
                new TextResourceContents("hotel://rooms", "text/markdown", list.toString())));
    }

    /**
     * Detailed information about a specific room.
     */
    @McpResource(uri = "hotel://rooms/{roomNumber}", name = "Room Detail", description = "Detailed information about a specific room including amenities and price.")
    public ReadResourceResult getRoomDetail(String roomNumber) {
        Optional<Room> roomOpt = roomService.findByRoomNumber(roomNumber);

        if (roomOpt.isEmpty()) {
            return new ReadResourceResult(List.of(
                    new TextResourceContents("hotel://rooms/" + roomNumber, "text/plain",
                            "Room " + roomNumber + " not found.")));
        }

        Room room = roomOpt.get();
        String details = String.format("""
                # Room %s Details

                - **Type:** %s
                - **Price per Night:** INR %.2f
                - **Max Occupancy:** %d persons
                - **Description:** %s
                - **Amenities:** %s
                - **Status:** %s
                """,
                room.getRoomNumber(),
                room.getType(),
                room.getPricePerNight(),
                room.getMaxOccupancy(),
                room.getDescription(),
                room.getAmenities(),
                room.isAvailable() ? "Available" : "Occupied/Maintenance");

        return new ReadResourceResult(List.of(
                new TextResourceContents("hotel://rooms/" + roomNumber, "text/markdown", details)));
    }
}
