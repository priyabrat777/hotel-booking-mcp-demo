package com.hotel.mcp.tools;

import com.hotel.mcp.dto.*;
import com.hotel.mcp.service.BookingService;
import com.hotel.mcp.service.RoomService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP Tools for Hotel Booking operations.
 * 
 * These tools are exposed to MCP clients (like Claude Desktop)
 * and allow AI assistants to help users with hotel room bookings.
 */
@Component
public class HotelBookingTools {

    private final RoomService roomService;
    private final BookingService bookingService;

    public HotelBookingTools(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    /**
     * List all available hotel room types with their prices and descriptions.
     * Use this tool to show customers what types of rooms are available.
     */
    @Tool(name = "list_room_types", description = "List all available hotel room types with their starting prices (in INR), descriptions, and current availability count. Use this to help guests explore room options.")
    public List<RoomTypeInfo> listRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    /**
     * Check room availability for specific dates and room type.
     * Use this to find available rooms for a guest's desired stay.
     */
    @Tool(name = "check_availability", description = "Check room availability for a specific room type and date range. Returns list of available rooms with prices in INR. Use this when a guest wants to know which rooms are available for their trip.")
    public AvailabilityResult checkAvailability(
            @ToolParam(description = "Room type to check. Valid values: SINGLE, DOUBLE, SUITE, DELUXE") String roomType,

            @ToolParam(description = "Check-in date in YYYY-MM-DD format (e.g., 2026-01-15)") String checkInDate,

            @ToolParam(description = "Check-out date in YYYY-MM-DD format (e.g., 2026-01-17)") String checkOutDate) {

        return roomService.checkAvailability(roomType, checkInDate, checkOutDate);
    }

    /**
     * Book a room for a guest.
     * Creates a pending booking that needs to be confirmed.
     */
    @Tool(name = "book_room", description = "Book a specific room for a guest. Creates a booking with PENDING status. Returns a booking reference that must be used to confirm the booking. Prices are in INR.")
    public BookingResult bookRoom(
            @ToolParam(description = "Room number to book (e.g., '201', '301')") String roomNumber,

            @ToolParam(description = "Full name of the guest making the booking") String guestName,

            @ToolParam(description = "Email address of the guest (for booking confirmation)") String guestEmail,

            @ToolParam(description = "Phone number of the guest (optional, for contact purposes)") String guestPhone,

            @ToolParam(description = "Check-in date in YYYY-MM-DD format") String checkInDate,

            @ToolParam(description = "Check-out date in YYYY-MM-DD format") String checkOutDate) {

        return bookingService.createBooking(roomNumber, guestName, guestEmail,
                guestPhone, checkInDate, checkOutDate);
    }

    /**
     * Confirm a pending booking.
     * Use this after creating a booking to finalize the reservation.
     */
    @Tool(name = "confirm_booking", description = "Confirm a pending booking using its booking reference. A booking must be confirmed for the reservation to be valid. Use this after creating a booking with book_room.")
    public ConfirmationResult confirmBooking(
            @ToolParam(description = "Booking reference code (e.g., 'HBK-20260112-A7B3')") String bookingReference) {

        return bookingService.confirmBooking(bookingReference);
    }

    /**
     * Get details of an existing booking.
     * Use this to retrieve information about a booking.
     */
    @Tool(name = "get_booking_details", description = "Retrieve complete details of an existing booking using its reference code. Use this when a guest wants to check their booking information.")
    public BookingDetails getBookingDetails(
            @ToolParam(description = "Booking reference code (e.g., 'HBK-20260112-A7B3')") String bookingReference) {

        return bookingService.getBookingDetails(bookingReference);
    }

    /**
     * Cancel an existing booking.
     * Use this when a guest needs to cancel their reservation.
     */
    @Tool(name = "cancel_booking", description = "Cancel an existing booking using its reference code. Use this when a guest wants to cancel their reservation.")
    public CancellationResult cancelBooking(
            @ToolParam(description = "Booking reference code (e.g., 'HBK-20260112-A7B3')") String bookingReference) {

        return bookingService.cancelBooking(bookingReference);
    }

    /**
     * Get occupancy report for a specific date.
     * Internal tool for hotel staff.
     */
    @Tool(name = "get_occupancy_report", description = "Get a detailed occupancy report for a specific date. Includes total rooms, occupied rooms, occupancy rate, and expected check-ins/outs. For hotel staff use only.")
    public OccupancyStats getOccupancyReport(
            @ToolParam(description = "Date for the report in YYYY-MM-DD format (e.g., 2026-01-14)") String date) {

        return bookingService.getOccupancyStats(date);
    }

    /**
     * Get revenue report for a date range.
     * Internal tool for hotel staff.
     */
    @Tool(name = "get_revenue_report", description = "Get a revenue report for a specific date range. Includes total confirmed revenue, number of bookings, and average daily rate. For hotel staff use only.")
    public RevenueStats getRevenueReport(
            @ToolParam(description = "Start date in YYYY-MM-DD format") String startDate,
            @ToolParam(description = "End date in YYYY-MM-DD format") String endDate) {

        return bookingService.getRevenueStats(startDate, endDate);
    }

    /**
     * Search for bookings by guest name, phone, or email.
     * Internal tool for hotel staff.
     */
    @Tool(name = "search_bookings", description = "Search for bookings using guest name, phone number, or email address. You can provide any combination of these filters. For hotel staff use only.")
    public List<BookingDetails> searchBookings(
            @ToolParam(description = "Guest name or part of name") String name,
            @ToolParam(description = "Guest phone number") String phone,
            @ToolParam(description = "Guest email address") String email) {

        return bookingService.searchBookings(name, phone, email);
    }
}
