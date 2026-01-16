package com.hotel.mcp.prompts;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP Prompts for Hotel Booking operations.
 */
@Component
public class HotelBookingPrompts {

    @McpPrompt(name = "welcoming_guest", description = "Generates a warm, personalized greeting for a guest's check-in.")
    public GetPromptResult welcomingGuest(
            @McpArg(name = "guestName", description = "Full name of the guest", required = true) String guestName,
            @McpArg(name = "roomNumber", description = "Assigned room number", required = true) String roomNumber) {

        String content = String.format("""
                Welcome to our hotel, %s!
                We are delighted to have you stay with us.
                Your room, %s, is ready for you.
                Is there anything I can assist you with right now?
                """, guestName, roomNumber);

        return new GetPromptResult(
                "Welcome Message",
                List.of(new PromptMessage(Role.ASSISTANT, new TextContent(content))));
    }

    @McpPrompt(name = "room_suggestion", description = "Suggests the best room type based on guest needs.")
    public GetPromptResult roomSuggestion(
            @McpArg(name = "preferences", description = "Guest preferences (e.g., quiet, view, accessibility)", required = true) String preferences,
            @McpArg(name = "numberOfGuests", description = "Number of guests staying", required = false) Integer numberOfGuests) {

        int guests = (numberOfGuests != null) ? numberOfGuests : 2;

        String content = String.format(
                """
                        A guest is looking for a room for %d people with these preferences: %s.
                        Please use the list_room_types tool to find the most suitable room type and explain why it's the best fit.
                        Include pricing and availability information in your response.
                        """,
                guests, preferences);

        return new GetPromptResult(
                "Room Suggestion Prompt",
                List.of(new PromptMessage(Role.USER, new TextContent(content))));
    }

    @McpPrompt(name = "booking_assistance", description = "Helps guests complete their booking with all necessary details.")
    public GetPromptResult bookingAssistance(
            @McpArg(name = "roomType", description = "Desired room type (SINGLE, DOUBLE, SUITE, DELUXE)", required = true) String roomType,
            @McpArg(name = "checkInDate", description = "Check-in date in YYYY-MM-DD format", required = true) String checkInDate,
            @McpArg(name = "checkOutDate", description = "Check-out date in YYYY-MM-DD format", required = true) String checkOutDate) {

        String content = String.format(
                """
                        A guest wants to book a %s room from %s to %s.
                        Please:
                        1. Check availability using the check_availability tool
                        2. Show available rooms with prices
                        3. Help them select a specific room
                        4. Guide them through providing guest details (name, email, phone)
                        5. Create the booking and explain the confirmation process
                        """,
                roomType, checkInDate, checkOutDate);

        return new GetPromptResult(
                "Booking Assistance Prompt",
                List.of(new PromptMessage(Role.USER, new TextContent(content))));
    }

    @McpPrompt(name = "modify_booking", description = "Assists guests with modifying or canceling their existing booking.")
    public GetPromptResult modifyBooking(
            @McpArg(name = "bookingReference", description = "Booking reference code (e.g., HBK-20260112-A7B3)", required = true) String bookingReference,
            @McpArg(name = "action", description = "Action to perform: view, cancel, or modify", required = true) String action) {

        String content = String.format(
                """
                        A guest wants to %s their booking with reference %s.
                        Please:
                        1. Retrieve the booking details using get_booking_details
                        2. Show them their current booking information
                        3. %s
                        4. Confirm the action and provide next steps
                        """,
                action, bookingReference,
                action.equalsIgnoreCase("cancel") 
                    ? "Process the cancellation using cancel_booking and explain the cancellation policy"
                    : action.equalsIgnoreCase("modify")
                    ? "Explain that modifications require canceling and creating a new booking, then guide them through the process"
                    : "Provide all booking details and ask if they need any changes");

        return new GetPromptResult(
                "Modify Booking Prompt",
                List.of(new PromptMessage(Role.USER, new TextContent(content))));
    }

    @McpPrompt(name = "special_requests", description = "Handles special guest requests and requirements.")
    public GetPromptResult specialRequests(
            @McpArg(name = "guestName", description = "Name of the guest", required = true) String guestName,
            @McpArg(name = "request", description = "Special request or requirement", required = true) String request) {

        String content = String.format(
                """
                        Guest %s has a special request: %s
                        
                        Please acknowledge their request warmly and:
                        1. Confirm if this can be accommodated
                        2. Provide relevant information from hotel resources if needed
                        3. Suggest alternative solutions if the exact request cannot be met
                        4. Assure them that their comfort is our priority
                        
                        Common requests we can handle:
                        - Early check-in / late checkout (subject to availability)
                        - Extra pillows, blankets, or towels
                        - Room location preferences (high floor, quiet area, near elevator)
                        - Dietary requirements for breakfast
                        - Airport transfer arrangements
                        - Special occasions (birthdays, anniversaries)
                        """,
                guestName, request);

        return new GetPromptResult(
                "Special Requests Prompt",
                List.of(new PromptMessage(Role.USER, new TextContent(content))));
    }
}
