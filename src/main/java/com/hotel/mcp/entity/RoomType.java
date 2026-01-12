package com.hotel.mcp.entity;

/**
 * Enum representing the different types of hotel rooms.
 */
public enum RoomType {
    SINGLE("Single Room", "Cozy room for solo travelers"),
    DOUBLE("Double Room", "Comfortable room for couples or friends"),
    SUITE("Suite", "Spacious suite with living area and premium amenities"),
    DELUXE("Deluxe Room", "Luxurious room with premium services and exclusive amenities");

    private final String displayName;
    private final String description;

    RoomType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
