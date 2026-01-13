package com.hotel.mcp.service;

import com.hotel.mcp.dto.*;
import com.hotel.mcp.entity.Booking;
import com.hotel.mcp.entity.BookingStatus;
import com.hotel.mcp.entity.Room;
import com.hotel.mcp.repository.BookingRepository;
import com.hotel.mcp.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Service for booking-related operations.
 */
@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final Random random = new Random();

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Create a new booking.
     */
    public BookingResult createBooking(String roomNumber, String guestName, String guestEmail,
            String guestPhone, String checkInStr, String checkOutStr) {
        log.info("Creating booking for room={}, guest={}", roomNumber, guestName);

        // Validate inputs
        if (guestName == null || guestName.trim().isEmpty()) {
            return BookingResult.failure("Guest name is required.");
        }
        if (guestEmail == null || !EMAIL_PATTERN.matcher(guestEmail).matches()) {
            return BookingResult.failure("Valid email address is required.");
        }

        // Parse dates
        LocalDate checkIn, checkOut;
        try {
            checkIn = LocalDate.parse(checkInStr);
            checkOut = LocalDate.parse(checkOutStr);
        } catch (DateTimeParseException e) {
            return BookingResult.failure("Invalid date format. Please use YYYY-MM-DD format.");
        }

        // Validate dates
        if (checkIn.isBefore(LocalDate.now())) {
            return BookingResult.failure("Check-in date cannot be in the past.");
        }
        if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
            return BookingResult.failure("Check-out date must be after check-in date.");
        }

        // Find room
        Optional<Room> roomOpt = roomRepository.findByRoomNumber(roomNumber);
        if (roomOpt.isEmpty()) {
            return BookingResult.failure("Room '" + roomNumber + "' not found.");
        }
        Room room = roomOpt.get();

        // Check room availability
        if (!room.isAvailable()) {
            return BookingResult.failure("Room '" + roomNumber + "' is not available for booking.");
        }

        boolean isAvailable = roomRepository.isRoomAvailableForDates(room.getId(), checkIn, checkOut);
        if (!isAvailable) {
            return BookingResult.failure("Room '" + roomNumber + "' is already booked for the selected dates.");
        }

        // Calculate pricing
        int numberOfNights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));

        // Generate booking reference
        String bookingReference = generateBookingReference();

        // Create booking
        Booking booking = new Booking(
                bookingReference,
                guestName.trim(),
                guestEmail.trim().toLowerCase(),
                guestPhone,
                room,
                checkIn,
                checkOut,
                totalPrice);

        bookingRepository.save(booking);
        log.info("Booking created: {}", bookingReference);

        return BookingResult.success(
                bookingReference,
                room.getRoomNumber(),
                room.getType().getDisplayName(),
                guestName,
                checkInStr,
                checkOutStr,
                numberOfNights,
                room.getPricePerNight(),
                totalPrice);
    }

    /**
     * Confirm a pending booking.
     */
    public ConfirmationResult confirmBooking(String bookingReference) {
        log.info("Confirming booking: {}", bookingReference);

        Optional<Booking> bookingOpt = bookingRepository.findByBookingReference(bookingReference);
        if (bookingOpt.isEmpty()) {
            return ConfirmationResult.notFound(bookingReference);
        }

        Booking booking = bookingOpt.get();

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return ConfirmationResult.alreadyConfirmed(bookingReference);
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return ConfirmationResult.failure(bookingReference,
                    "Cannot confirm a cancelled booking.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        log.info("Booking confirmed: {}", bookingReference);

        return ConfirmationResult.success(
                bookingReference,
                booking.getGuestName(),
                booking.getRoom().getRoomNumber(),
                booking.getCheckInDate().format(DATE_FORMATTER),
                booking.getCheckOutDate().format(DATE_FORMATTER));
    }

    /**
     * Get booking details by reference.
     */
    @Transactional(readOnly = true)
    public BookingDetails getBookingDetails(String bookingReference) {
        log.info("Fetching booking details: {}", bookingReference);

        return bookingRepository.findByBookingReference(bookingReference)
                .map(BookingDetails::from)
                .orElse(BookingDetails.notFound(bookingReference));
    }

    /**
     * Cancel a booking.
     */
    public CancellationResult cancelBooking(String bookingReference) {
        log.info("Cancelling booking: {}", bookingReference);

        Optional<Booking> bookingOpt = bookingRepository.findByBookingReference(bookingReference);
        if (bookingOpt.isEmpty()) {
            return CancellationResult.notFound(bookingReference);
        }

        Booking booking = bookingOpt.get();

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return CancellationResult.alreadyCancelled(bookingReference);
        }

        String previousStatus = booking.getStatus().getDisplayName();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking cancelled: {}", bookingReference);

        return CancellationResult.success(bookingReference, previousStatus);
    }

    /**
     * Get occupancy statistics for a specific date.
     */
    @Transactional(readOnly = true)
    public OccupancyStats getOccupancyStats(String dateStr) {
        log.info("Fetching occupancy stats for date: {}", dateStr);
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}", dateStr);
            return null; // Or throw an exception
        }

        long totalRooms = roomRepository.count();
        long occupiedRooms = bookingRepository.countActiveBookingsOnDate(date);

        // Expected check-ins/check-outs for the day
        long checkIns = bookingRepository.findActiveBookings().stream()
                .filter(b -> b.getCheckInDate().equals(date))
                .count();
        long checkOuts = bookingRepository.findActiveBookings().stream()
                .filter(b -> b.getCheckOutDate().equals(date))
                .count();

        return OccupancyStats.of(date, totalRooms, occupiedRooms, checkIns, checkOuts);
    }

    /**
     * Get revenue statistics for a date range.
     */
    @Transactional(readOnly = true)
    public RevenueStats getRevenueStats(String startDateStr, String endDateStr) {
        log.info("Fetching revenue stats from {} to {}", startDateStr, endDateStr);
        LocalDate start, end;
        try {
            start = LocalDate.parse(startDateStr);
            end = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException e) {
            log.error("Invalid date range: {} to {}", startDateStr, endDateStr);
            return null;
        }

        BigDecimal totalRevenue = bookingRepository.sumRevenueInDateRange(start, end);
        if (totalRevenue == null)
            totalRevenue = BigDecimal.ZERO;

        long count = bookingRepository.findBookingsInDateRange(start, end).stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();

        return RevenueStats.of(start, end, totalRevenue, count);
    }

    /**
     * Search bookings by name, phone, or email.
     */
    @Transactional(readOnly = true)
    public java.util.List<BookingDetails> searchBookings(String name, String phone, String email) {
        log.info("Searching bookings by name={}, phone={}, email={}", name, phone, email);
        java.util.Set<Booking> results = new java.util.HashSet<>();

        if (name != null && !name.isBlank()) {
            results.addAll(bookingRepository.findByGuestNameContainingIgnoreCase(name.trim()));
        }
        if (phone != null && !phone.isBlank()) {
            results.addAll(bookingRepository.findByGuestPhoneContaining(phone.trim()));
        }
        if (email != null && !email.isBlank()) {
            results.addAll(bookingRepository.findByGuestEmailContainingIgnoreCase(email.trim().toLowerCase()));
        }

        return results.stream()
                .map(BookingDetails::from)
                .sorted(java.util.Comparator.comparing(BookingDetails::checkInDate))
                .toList();
    }

    /**
     * Generate a unique booking reference.
     * Format: HBK-YYYYMMDD-XXXX (e.g., HBK-20260112-A7B3)
     */
    private String generateBookingReference() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04X", random.nextInt(0xFFFF));
        return "HBK-" + datePart + "-" + randomPart;
    }
}
