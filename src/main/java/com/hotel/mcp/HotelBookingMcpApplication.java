package com.hotel.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hotel Booking MCP Server Application.
 * 
 * This application exposes hotel booking functionality as MCP tools
 * that can be used by Claude Desktop and other MCP clients.
 */
@SpringBootApplication
public class HotelBookingMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingMcpApplication.class, args);
    }
}
