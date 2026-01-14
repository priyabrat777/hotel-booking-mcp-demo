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
}
