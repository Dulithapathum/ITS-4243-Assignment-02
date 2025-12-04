package com.travel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {
    private Long id;
    private String name;
    private String location;
    private String address;
    private Integer starRating;
    private BigDecimal pricePerNight;
    private Integer availableRooms;
    private Integer totalRooms;
    private String amenities;
    private Boolean available;
}

