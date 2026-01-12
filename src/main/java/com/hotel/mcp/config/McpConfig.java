package com.hotel.mcp.config;

import com.hotel.mcp.dto.RoomTypeInfo;
import com.hotel.mcp.service.RoomService;
import com.hotel.mcp.tools.HotelBookingTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
public class McpConfig {

    @Bean
    @Description("List all available hotel room types with their starting prices (in INR), descriptions, and current availability count. Use this to help guests explore room options.")
    public List<ToolCallback> listRoomTypes(HotelBookingTools hotelBookingTools) {
        return List.of(ToolCallbacks.from(hotelBookingTools));
    }
}
