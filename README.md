# Hotel Booking MCP Server

A **Model Context Protocol (MCP) Server** for hotel room booking operations, designed to work seamlessly with **Claude Desktop**. Built with Java/Spring Boot and Spring AI MCP Server.

## 🏨 Features

- **List Room Types** - View all available room categories with prices (in INR)
- **Check Availability** - Find available rooms for specific dates
- **Book Rooms** - Create reservations with guest details
- **Confirm Bookings** - Finalize pending reservations
- **View Booking Details** - Look up existing reservations
- **Cancel Bookings** - Cancel reservations when needed

## 📋 Prerequisites

- **Java 21** or later
- **Docker** and **Docker Compose** (for PostgreSQL)
- **Claude Desktop** (for MCP integration)
- **Maven 3.9+** (or use the included wrapper)

## 🚀 Quick Start

### 1. Clone and Build

```bash
# Navigate to the project directory
cd hotel-booking-mcp-demo

# Build the application
./mvnw clean package -DskipTests
```

### 2. Start PostgreSQL Database

```bash
# Start PostgreSQL using Docker Compose
docker-compose -f docker-compose.dev.yml up -d

# Verify the database is running
docker ps
```

### 3. Run the Application (Development)

For development/testing without Claude Desktop:

```bash
# Run with H2 in-memory database
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# OR run with PostgreSQL
./mvnw spring-boot:run
```

### 4. Configure Claude Desktop

1. Locate your Claude Desktop configuration file:
   - **macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
   - **Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

2. Add the hotel booking server configuration:

```json
{
  "mcpServers": {
    "hotel-booking": {
      "command": "java",
      "args": [
        "-jar",
        "/full/path/to/hotel-booking-mcp-demo/target/hotel-booking-mcp-server-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "SPRING_DATASOURCE_URL": "jdbc:postgresql://localhost:5432/hoteldb",
        "SPRING_DATASOURCE_USERNAME": "hotel",
        "SPRING_DATASOURCE_PASSWORD": "hotel123"
      }
    }
  }
}
```

> **Note**: Replace `/full/path/to/` with the actual absolute path to your project.

3. Restart Claude Desktop

4. Start a conversation and ask about hotel bookings!

### 5. Test with MCP Inspector

The [MCP Inspector](https://github.com/modelcontextprotocol/inspector) is an interactive tool for testing MCP servers.

```bash
# Run inspector with the application JAR
npx @modelcontextprotocol/inspector java -jar target/hotel-booking-mcp-server-1.0.0-SNAPSHOT.jar
```

This will start a web interface (usually at `http://localhost:3000`) where you can:
- View all available tools
- Execute tools with custom parameters
- Inspect raw JSON requests and responses

> [!TIP]
> Make sure PostgreSQL is running before starting the inspector if you are using the default profile.

## 🛠️ MCP Tools Reference

The server exposes the following tools that Claude can use:

### `list_room_types`
Lists all available hotel room types with starting prices.

**Example prompt**: "What types of rooms do you have available?"

**Response includes**:
- Room type name and description
- Starting price per night (INR)
- Maximum occupancy
- Number of available rooms

### `check_availability`
Checks room availability for specific dates and room type.

**Parameters**:
- `roomType` - SINGLE, DOUBLE, SUITE, or DELUXE
- `checkInDate` - Format: YYYY-MM-DD
- `checkOutDate` - Format: YYYY-MM-DD

**Example prompt**: "Are there any double rooms available from January 15-17, 2026?"

### `book_room`
Creates a new room booking (status: PENDING).

**Parameters**:
- `roomNumber` - The specific room to book (e.g., "201")
- `guestName` - Full name of the guest
- `guestEmail` - Email address
- `guestPhone` - Phone number (optional)
- `checkInDate` - Format: YYYY-MM-DD
- `checkOutDate` - Format: YYYY-MM-DD

**Example prompt**: "Book room 201 for John Doe, email john@example.com, phone 9876543210, from January 15-17, 2026"

### `confirm_booking`
Confirms a pending booking.

**Parameters**:
- `bookingReference` - The booking code (e.g., "HBK-20260112-A7B3")

**Example prompt**: "Confirm booking HBK-20260112-A7B3"

### `get_booking_details`
Retrieves details of an existing booking.

**Parameters**:
- `bookingReference` - The booking code

**Example prompt**: "Show me the details for booking HBK-20260112-A7B3"

### `cancel_booking`
Cancels an existing booking.

**Parameters**:
- `bookingReference` - The booking code

**Example prompt**: "Cancel booking HBK-20260112-A7B3"

### `get_occupancy_report` [NEW]
Provides a detailed occupancy report for a specific date. (Staff Tool)

**Parameters**:
- `date` - Format: YYYY-MM-DD

**Example prompt**: "Give me the occupancy report for 2026-01-14"

### `get_revenue_report` [NEW]
Calculates total confirmed revenue and average daily rate for a period. (Staff Tool)

**Parameters**:
- `startDate` - Format: YYYY-MM-DD
- `endDate` - Format: YYYY-MM-DD

**Example prompt**: "Show me the revenue report from Jan 1st to Jan 7th, 2026"

### `search_bookings` [NEW]
Searches for reservations using guest details. (Staff Tool)

**Parameters**:
- `name` - Guest name (partial match)
- `phone` - Guest phone number
- `email` - Guest email address

**Example prompt**: "Search for bookings under the name 'Priyabrat' or email 'priya@example.com'"

## 💬 Example Conversation

```
You: What room types do you have?

Claude: Let me check the available room types for you.
[Uses list_room_types tool]

Here are our available room types:

| Type   | Starting Price | Max Guests | Available |
|--------|----------------|------------|-----------|
| Single | ₹2,500/night   | 1          | 4 rooms   |
| Double | ₹4,500/night   | 2          | 4 rooms   |
| Suite  | ₹8,500/night   | 4          | 4 rooms   |
| Deluxe | ₹15,000/night  | 4-6        | 4 rooms   |

You: Check availability for a double room from Jan 20-22, 2026

Claude: [Uses check_availability tool]

Great news! 4 double rooms are available for Jan 20-22, 2026:
- Room 201: ₹4,500/night - Twin beds, Mini Bar
- Room 202: ₹4,500/night - Queen bed, Mini Bar
- Room 203: ₹5,000/night - Balcony, City View
- Room 204: ₹5,000/night - Balcony, Pool View

You: Book room 203 for Priya Sharma, email priya@example.com

Claude: [Uses book_room tool]

Booking created successfully!
- Reference: HBK-20260112-B4C2
- Room: 203 (Double Room with Balcony)
- Guest: Priya Sharma
- Check-in: Jan 20, 2026
- Check-out: Jan 22, 2026
- Total: ₹10,000 (2 nights × ₹5,000)
- Status: PENDING

Please confirm this booking to finalize your reservation.

You: Confirm booking HBK-20260112-B4C2

Claude: [Uses confirm_booking tool]

Your booking is confirmed! 🎉
- Reference: HBK-20260112-B4C2
- We look forward to welcoming Priya Sharma on Jan 20, 2026!
```

## 🐳 Docker Deployment

### Full Stack Deployment

```bash
# Build and run everything with Docker Compose
docker-compose up --build

# Stop the services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Development Mode (PostgreSQL only)

```bash
# Start only PostgreSQL
docker-compose -f docker-compose.dev.yml up -d

# Run the application locally
./mvnw spring-boot:run
```

## 📁 Project Structure

```
hotel-booking-mcp-demo/
├── pom.xml                          # Maven build configuration
├── Dockerfile                       # Multi-stage Docker build
├── docker-compose.yml               # Full stack deployment
├── docker-compose.dev.yml           # PostgreSQL only
├── claude_desktop_config.example.json
├── README.md
└── src/main/
    ├── java/com/hotel/mcp/
    │   ├── HotelBookingMcpApplication.java
    │   ├── config/
    │   │   └── DataInitializer.java    # Loads dummy data
    │   ├── entity/
    │   │   ├── Room.java
    │   │   ├── Booking.java
    │   │   ├── RoomType.java
    │   │   └── BookingStatus.java
    │   ├── repository/
    │   │   ├── RoomRepository.java
    │   │   └── BookingRepository.java
    │   ├── service/
    │   │   ├── RoomService.java
    │   │   └── BookingService.java
    │   ├── dto/                        # Response DTOs
    │   │   ├── RoomTypeInfo.java
    │   │   ├── AvailabilityResult.java
    │   │   ├── BookingResult.java
    │   │   └── ...
    │   └── tools/
    │       └── HotelBookingTools.java  # MCP Tools
    └── resources/
        └── application.yml
```

## 🏨 Pre-loaded Room Data

The application initializes with 16 rooms:

| Room  | Type   | Price (₹) | Amenities                           |
|-------|--------|-----------|-------------------------------------|
| 101   | Single | 2,500     | AC, WiFi, TV, Work Desk             |
| 102   | Single | 2,500     | AC, WiFi, TV, Work Desk             |
| 103   | Single | 2,800     | AC, WiFi, TV, City View, Balcony    |
| 104   | Single | 2,800     | AC, WiFi, TV, Mountain View         |
| 201   | Double | 4,500     | AC, WiFi, TV, Mini Bar, Coffee      |
| 202   | Double | 4,500     | AC, WiFi, TV, Mini Bar, Coffee      |
| 203   | Double | 5,000     | AC, WiFi, TV, Balcony, City View    |
| 204   | Double | 5,000     | AC, WiFi, TV, Balcony, Pool View    |
| 301   | Suite  | 8,500     | Kitchen, Living Room, Dining        |
| 302   | Suite  | 8,500     | Kitchen, Living Room, 2 Bedrooms    |
| 303   | Suite  | 9,500     | Kitchen, Pool View, Jacuzzi Tub     |
| 304   | Suite  | 9,500     | Kitchen, City View, Office Space    |
| 401   | Deluxe | 15,000    | Jacuzzi, Butler, Lounge Access      |
| 402   | Deluxe | 15,000    | Private Terrace, Butler, Spa        |
| 501   | Deluxe | 25,000    | Private Pool, Butler, Chef Service  |
| 502   | Deluxe | 25,000    | Rooftop Terrace, Butler, Limousine  |

## 🔧 Configuration

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.url` | Database URL | `jdbc:postgresql://localhost:5432/hoteldb` |
| `spring.datasource.username` | Database user | `hotel` |
| `spring.datasource.password` | Database password | `hotel123` |
| `spring.ai.mcp.server.stdio` | Enable STDIO transport | `true` |

### Environment Variables

```bash
# Database configuration
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hoteldb
export SPRING_DATASOURCE_USERNAME=hotel
export SPRING_DATASOURCE_PASSWORD=hotel123

# Run with environment variables
java -jar target/hotel-booking-mcp-server-1.0.0-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### Claude Desktop not detecting the server

1. Verify the JAR path in `claude_desktop_config.json` is correct and absolute
2. Ensure PostgreSQL is running: `docker ps`
3. Check Java is available: `java -version`
4. Restart Claude Desktop completely

### Database connection issues

1. Verify PostgreSQL is running: `docker-compose -f docker-compose.dev.yml ps`
2. Check connection: `docker exec -it hotel-booking-db-dev psql -U hotel -d hoteldb`
3. Verify environment variables are set correctly

### Application won't start

1. Check for port conflicts
2. Verify Java 21 is installed
3. Review logs: `./mvnw spring-boot:run 2>&1 | tee app.log`

## 📄 License

This project is for demonstration purposes.

## 🤝 Contributing

Feel free to open issues or submit pull requests for improvements!
