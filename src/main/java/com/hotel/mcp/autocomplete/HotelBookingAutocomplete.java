package com.hotel.mcp.autocomplete;

import com.hotel.mcp.entity.Booking;
import com.hotel.mcp.entity.Room;
import com.hotel.mcp.repository.BookingRepository;
import com.hotel.mcp.repository.RoomRepository;
import org.springaicommunity.mcp.annotation.McpComplete;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Autocomplete handlers for Hotel Booking MCP prompts.
 * 
 * Provides intelligent autocomplete suggestions for prompt arguments,
 * making it easier for AI clients to fill in booking details.
 */
@Component
public class HotelBookingAutocomplete {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public HotelBookingAutocomplete(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Autocomplete for room numbers in the welcoming_guest prompt.
     * Suggests all room numbers with their types and floor information.
     */
    @McpComplete(prompt = "welcoming_guest")
    public List<String> completeWelcomingGuestRoomNumber(String partial) {
        List<Room> rooms = roomRepository.findAll();
        
        return rooms.stream()
                .map(room -> room.getRoomNumber() + " - " + room.getType().getDisplayName() 
                        + " (Floor " + room.getRoomNumber().charAt(0) + ")")
                .filter(suggestion -> partial == null || partial.isEmpty() 
                        || suggestion.toLowerCase().contains(partial.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for guest names in the welcoming_guest prompt.
     * Suggests recent guest names from confirmed bookings.
     */
    @McpComplete(prompt = "welcoming_guest")
    public List<String> completeWelcomingGuestName(String partial) {
        List<Booking> recentBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == com.hotel.mcp.entity.BookingStatus.CONFIRMED)
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(20)
                .toList();
        
        return recentBookings.stream()
                .map(Booking::getGuestName)
                .distinct()
                .filter(name -> partial == null || partial.isEmpty() 
                        || name.toLowerCase().contains(partial.toLowerCase()))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for preferences in the room_suggestion prompt.
     * Suggests common guest preferences and requirements.
     */
    @McpComplete(prompt = "room_suggestion")
    public List<String> completeRoomPreferences(String partial) {
        List<String> commonPreferences = Arrays.asList(
                "quiet location",
                "city view",
                "mountain view",
                "pool view",
                "garden view",
                "balcony required",
                "ground floor access",
                "high floor preferred",
                "near elevator",
                "away from elevator",
                "wheelchair accessible",
                "family with children",
                "business traveler - work desk needed",
                "romantic getaway",
                "budget-conscious",
                "luxury amenities",
                "kitchenette required",
                "extra space needed",
                "connecting rooms",
                "late check-in"
        );
        
        return commonPreferences.stream()
                .filter(pref -> partial == null || partial.isEmpty() 
                        || pref.toLowerCase().contains(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for number of guests in the room_suggestion prompt.
     * Suggests guest counts with helpful context.
     */
    @McpComplete(prompt = "room_suggestion")
    public List<String> completeNumberOfGuests(String partial) {
        List<String> guestOptions = Arrays.asList(
                "1 - Solo traveler",
                "2 - Couple or friends",
                "3 - Small family or group",
                "4 - Family of four",
                "5 - Large family",
                "6 - Group booking"
        );
        
        return guestOptions.stream()
                .filter(option -> partial == null || partial.isEmpty() 
                        || option.toLowerCase().contains(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for room types in the booking_assistance prompt.
     * Suggests all available room types with descriptions.
     */
    @McpComplete(prompt = "booking_assistance")
    public List<String> completeBookingRoomType(String partial) {
        return Arrays.asList(
                "SINGLE - Cozy room for solo travelers",
                "DOUBLE - Comfortable room for couples or friends",
                "SUITE - Spacious suite with living area and premium amenities",
                "DELUXE - Luxurious room with premium services and exclusive amenities"
        ).stream()
                .filter(option -> partial == null || partial.isEmpty() 
                        || option.toLowerCase().contains(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for check-in dates in the booking_assistance prompt.
     * Suggests upcoming dates for the next 30 days.
     */
    @McpComplete(prompt = "booking_assistance")
    public List<String> completeBookingCheckInDate(String partial) {
        List<String> suggestions = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        
        // Add today and next 14 days
        for (int i = 0; i <= 14; i++) {
            java.time.LocalDate date = today.plusDays(i);
            String dateStr = date.toString();
            String label = i == 0 ? "Today" : i == 1 ? "Tomorrow" : date.getDayOfWeek().toString();
            suggestions.add(dateStr + " (" + capitalize(label) + ")");
        }
        
        return suggestions.stream()
                .filter(s -> partial == null || partial.isEmpty() || s.contains(partial))
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for check-out dates in the booking_assistance prompt.
     * Suggests dates starting from tomorrow.
     */
    @McpComplete(prompt = "booking_assistance")
    public List<String> completeBookingCheckOutDate(String partial) {
        List<String> suggestions = new java.util.ArrayList<>();
        java.time.LocalDate tomorrow = java.time.LocalDate.now().plusDays(1);
        
        // Add tomorrow and next 14 days
        for (int i = 0; i <= 14; i++) {
            java.time.LocalDate date = tomorrow.plusDays(i);
            String dateStr = date.toString();
            suggestions.add(dateStr + " (" + date.getDayOfWeek().toString() + ")");
        }
        
        return suggestions.stream()
                .filter(s -> partial == null || partial.isEmpty() || s.contains(partial))
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for booking references in the modify_booking prompt.
     * Suggests recent active booking references.
     */
    @McpComplete(prompt = "modify_booking")
    public List<String> completeModifyBookingReference(String partial) {
        List<Booking> recentBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() != com.hotel.mcp.entity.BookingStatus.CANCELLED)
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(15)
                .toList();
        
        return recentBookings.stream()
                .map(booking -> booking.getBookingReference() + " - " 
                        + booking.getGuestName() + " (Room " + booking.getRoom().getRoomNumber() + ")")
                .filter(suggestion -> partial == null || partial.isEmpty() 
                        || suggestion.toLowerCase().contains(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for action in the modify_booking prompt.
     * Suggests available actions for booking modifications.
     */
    @McpComplete(prompt = "modify_booking")
    public List<String> completeModifyAction(String partial) {
        return Arrays.asList(
                "view - View booking details",
                "cancel - Cancel the booking",
                "modify - Modify booking dates or room"
        ).stream()
                .filter(option -> partial == null || partial.isEmpty() 
                        || option.toLowerCase().contains(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for guest names in the special_requests prompt.
     * Suggests recent guest names from bookings.
     */
    @McpComplete(prompt = "special_requests")
    public List<String> completeSpecialRequestGuestName(String partial) {
        List<Booking> recentBookings = bookingRepository.findAll().stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(30)
                .toList();
        
        return recentBookings.stream()
                .map(Booking::getGuestName)
                .distinct()
                .filter(name -> partial == null || partial.isEmpty() 
                        || name.toLowerCase().contains(partial.toLowerCase()))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete for special requests.
     * Suggests common guest requests.
     */
    @McpComplete(prompt = "special_requests")
    public List<String> completeSpecialRequest(String partial) {
        return Arrays.asList(
                "Early check-in (before 2 PM)",
                "Late checkout (after 11 AM)",
                "Extra pillows and blankets",
                "High floor room",
                "Quiet room away from elevator",
                "Room near elevator",
                "Airport transfer service",
                "Birthday celebration setup",
                "Anniversary surprise",
                "Vegetarian breakfast",
                "Gluten-free meal options",
                "Extra towels",
                "Iron and ironing board",
                "Baby cot in room",
                "Connecting rooms for family"
        ).stream()
                .filter(request -> partial == null || partial.isEmpty() 
                        || request.toLowerCase().contains(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to capitalize first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
