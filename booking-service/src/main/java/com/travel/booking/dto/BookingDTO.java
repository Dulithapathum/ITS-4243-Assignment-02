package com.travel.booking.dto;

import com.travel.booking.entity.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    
    private Long id;
    private Long userId;
    private Long flightId;
    private Long hotelId;
    private LocalDate travelDate;
    private BigDecimal flightPrice;
    private BigDecimal hotelPrice;
    private BigDecimal totalCost;
    private BookingStatus status;
    private Long paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

