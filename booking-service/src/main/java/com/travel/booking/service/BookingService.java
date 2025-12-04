package com.travel.booking.service;

import com.travel.booking.client.FlightServiceClient;
import com.travel.booking.client.HotelServiceClient;
import com.travel.booking.dto.*;
import com.travel.booking.entity.Booking;
import com.travel.booking.entity.Booking.BookingStatus;
import com.travel.booking.exception.BookingException;
import com.travel.booking.exception.ResourceNotFoundException;
import com.travel.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final FlightServiceClient flightServiceClient;
    private final HotelServiceClient hotelServiceClient;
    
    @Qualifier("userWebClient")
    private final WebClient userWebClient;
    
    @Qualifier("notificationWebClient")
    private final WebClient notificationWebClient;
    
    @Transactional
    public BookingDTO createBooking(BookingRequest request) {
        log.info("Starting booking process for user: {}", request.getUserId());
        
        // Step 1: Validate user via WebClient
        log.info("Step 1: Validating user");
        boolean isValidUser = validateUser(request.getUserId());
        if (!isValidUser) {
            throw new BookingException("User validation failed. User ID: " + request.getUserId());
        }
        UserDTO user = getUserDetails(request.getUserId());
        
        // Step 2: Check flight availability via Feign Client
        log.info("Step 2: Checking flight availability");
        ApiResponse<Map<String, Boolean>> flightAvailability = 
                flightServiceClient.checkAvailability(request.getFlightId());
        if (flightAvailability.getData() == null || !flightAvailability.getData().get("available")) {
            throw new BookingException("Flight is not available. Flight ID: " + request.getFlightId());
        }
        ApiResponse<FlightDTO> flightResponse = flightServiceClient.getFlightById(request.getFlightId());
        FlightDTO flight = flightResponse.getData();
        
        // Step 3: Check hotel availability via Feign Client
        log.info("Step 3: Checking hotel availability");
        ApiResponse<Map<String, Boolean>> hotelAvailability = 
                hotelServiceClient.checkAvailability(request.getHotelId());
        if (hotelAvailability.getData() == null || !hotelAvailability.getData().get("available")) {
            throw new BookingException("Hotel is not available. Hotel ID: " + request.getHotelId());
        }
        ApiResponse<HotelDTO> hotelResponse = hotelServiceClient.getHotelById(request.getHotelId());
        HotelDTO hotel = hotelResponse.getData();
        
        // Step 4: Calculate total cost
        log.info("Step 4: Calculating total cost");
        BigDecimal totalCost = flight.getPrice().add(hotel.getPricePerNight());
        
        // Step 5: Create and save booking as PENDING
        log.info("Step 5: Creating booking as PENDING");
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setFlightId(request.getFlightId());
        booking.setHotelId(request.getHotelId());
        booking.setTravelDate(request.getTravelDate());
        booking.setFlightPrice(flight.getPrice());
        booking.setHotelPrice(hotel.getPricePerNight());
        booking.setTotalCost(totalCost);
        booking.setStatus(BookingStatus.PENDING);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Step 6: Reserve flight and hotel
        log.info("Step 6: Reserving flight and hotel");
        try {
            flightServiceClient.reserveSeat(request.getFlightId());
            hotelServiceClient.reserveRoom(request.getHotelId());
        } catch (Exception e) {
            log.error("Failed to reserve resources", e);
            savedBooking.setStatus(BookingStatus.FAILED);
            bookingRepository.save(savedBooking);
            throw new BookingException("Failed to reserve resources: " + e.getMessage());
        }
        
        // Step 7: Send notification via WebClient
        log.info("Step 7: Sending booking notification");
        sendNotification(user, savedBooking, "Booking Created", 
                "Your booking has been created and is pending payment.");
        
        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        return mapToDTO(savedBooking);
    }
    
    private boolean validateUser(Long userId) {
        try {
            ApiResponse response = userWebClient.get()
                    .uri("/api/users/{id}/validate", userId)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();
            
            if (response != null && response.getData() != null) {
                Map<String, Boolean> validationData = (Map<String, Boolean>) response.getData();
                return validationData.get("valid");
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to validate user", e);
            return false;
        }
    }
    
    private UserDTO getUserDetails(Long userId) {
        try {
            ApiResponse<UserDTO> response = userWebClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();
            
            if (response != null && response.getData() != null) {
                Map<String, Object> userData = (Map<String, Object>) response.getData();
                UserDTO user = new UserDTO();
                user.setId(((Number) userData.get("id")).longValue());
                user.setName((String) userData.get("name"));
                user.setEmail((String) userData.get("email"));
                user.setPhone((String) userData.get("phone"));
                user.setAddress((String) userData.get("address"));
                return user;
            }
            throw new BookingException("Failed to fetch user details");
        } catch (Exception e) {
            log.error("Failed to get user details", e);
            throw new BookingException("Failed to fetch user details: " + e.getMessage());
        }
    }
    
    private void sendNotification(UserDTO user, Booking booking, String subject, String message) {
        try {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(user.getId());
            notificationRequest.setBookingId(booking.getId());
            notificationRequest.setRecipient(user.getEmail());
            notificationRequest.setSubject(subject);
            notificationRequest.setMessage(message);
            notificationRequest.setType("EMAIL");
            
            notificationWebClient.post()
                    .uri("/api/notifications/send")
                    .bodyValue(notificationRequest)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();
            
            log.info("Notification sent for booking: {}", booking.getId());
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            // Don't throw exception, just log the error
        }
    }
    
    @Transactional
    public BookingDTO updateBookingStatus(Long bookingId, BookingStatusUpdateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        booking.setStatus(BookingStatus.valueOf(request.getStatus()));
        if (request.getPaymentId() != null) {
            booking.setPaymentId(request.getPaymentId());
        }
        
        Booking updatedBooking = bookingRepository.save(booking);
        
        // Send notification about status change
        try {
            UserDTO user = getUserDetails(booking.getUserId());
            String message = "Your booking status has been updated to: " + request.getStatus();
            sendNotification(user, booking, "Booking Status Updated", message);
        } catch (Exception e) {
            log.error("Failed to send status update notification", e);
        }
        
        return mapToDTO(updatedBooking);
    }
    
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToDTO(booking);
    }
    
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        try {
            UserDTO user = getUserDetails(booking.getUserId());
            sendNotification(user, booking, "Booking Cancelled", 
                    "Your booking has been cancelled.");
        } catch (Exception e) {
            log.error("Failed to send cancellation notification", e);
        }
    }
    
    private BookingDTO mapToDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getUserId(),
                booking.getFlightId(),
                booking.getHotelId(),
                booking.getTravelDate(),
                booking.getFlightPrice(),
                booking.getHotelPrice(),
                booking.getTotalCost(),
                booking.getStatus(),
                booking.getPaymentId(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}

