package com.hotel.mcp.config;

import com.hotel.mcp.entity.Room;
import com.hotel.mcp.entity.RoomType;
import com.hotel.mcp.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Initializes the database with sample hotel room data.
 * This runs on application startup and loads dummy data for testing.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoomRepository roomRepository;

    public DataInitializer(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (roomRepository.count() > 0) {
            log.info("Database already initialized with {} rooms", roomRepository.count());
            return;
        }

        log.info("Initializing database with sample hotel room data...");

        List<Room> rooms = List.of(
                // Single Rooms (Floor 1)
                new Room("101", RoomType.SINGLE, new BigDecimal("2500.00"),
                        "Cozy single room with city view",
                        "AC, WiFi, TV, Work Desk", 1),
                new Room("102", RoomType.SINGLE, new BigDecimal("2500.00"),
                        "Comfortable single room with garden view",
                        "AC, WiFi, TV, Work Desk", 1),
                new Room("103", RoomType.SINGLE, new BigDecimal("2800.00"),
                        "Premium single room with city view and balcony",
                        "AC, WiFi, TV, City View, Balcony", 1),
                new Room("104", RoomType.SINGLE, new BigDecimal("2800.00"),
                        "Premium single room with mountain view",
                        "AC, WiFi, TV, Mountain View, Balcony", 1),

                // Double Rooms (Floor 2)
                new Room("201", RoomType.DOUBLE, new BigDecimal("4500.00"),
                        "Spacious double room with twin beds",
                        "AC, WiFi, TV, Mini Bar, Coffee Maker", 2),
                new Room("202", RoomType.DOUBLE, new BigDecimal("4500.00"),
                        "Comfortable double room with queen bed",
                        "AC, WiFi, TV, Mini Bar, Coffee Maker", 2),
                new Room("203", RoomType.DOUBLE, new BigDecimal("5000.00"),
                        "Premium double room with balcony and city view",
                        "AC, WiFi, TV, Mini Bar, Balcony, City View", 2),
                new Room("204", RoomType.DOUBLE, new BigDecimal("5000.00"),
                        "Premium double room with private balcony",
                        "AC, WiFi, TV, Mini Bar, Balcony, Pool View", 2),

                // Suite Rooms (Floor 3)
                new Room("301", RoomType.SUITE, new BigDecimal("8500.00"),
                        "Elegant suite with separate living area",
                        "AC, WiFi, TV, Kitchen, Living Room, Dining Area", 4),
                new Room("302", RoomType.SUITE, new BigDecimal("8500.00"),
                        "Family suite with two bedrooms",
                        "AC, WiFi, TV, Kitchen, Living Room, 2 Bedrooms", 4),
                new Room("303", RoomType.SUITE, new BigDecimal("9500.00"),
                        "Premium suite with pool view",
                        "AC, WiFi, TV, Kitchen, Pool View, Jacuzzi Tub", 4),
                new Room("304", RoomType.SUITE, new BigDecimal("9500.00"),
                        "Executive suite with panoramic city view",
                        "AC, WiFi, TV, Kitchen, City View, Office Space", 4),

                // Deluxe Rooms (Floor 4 & 5)
                new Room("401", RoomType.DELUXE, new BigDecimal("15000.00"),
                        "Luxurious deluxe room with premium amenities",
                        "Premium Suite, Jacuzzi, Butler Service, Lounge Access", 4),
                new Room("402", RoomType.DELUXE, new BigDecimal("15000.00"),
                        "Deluxe room with private terrace",
                        "Premium Suite, Private Terrace, Butler Service, Spa Access", 4),
                new Room("501", RoomType.DELUXE, new BigDecimal("25000.00"),
                        "Presidential suite with private pool",
                        "Presidential Suite, Private Pool, Butler, Chef Service", 6),
                new Room("502", RoomType.DELUXE, new BigDecimal("25000.00"),
                        "Royal suite with panoramic views",
                        "Royal Suite, Rooftop Terrace, Butler, Limousine Service", 6));

        roomRepository.saveAll(rooms);
        log.info("Successfully initialized {} hotel rooms", rooms.size());
    }
}
