package com.travel.booking.client;

import com.travel.booking.dto.ApiResponse;
import com.travel.booking.dto.HotelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "hotel-service", url = "${services.hotel.url}")
public interface HotelServiceClient {
    
    @GetMapping("/api/hotels/{id}")
    ApiResponse<HotelDTO> getHotelById(@PathVariable("id") Long id);
    
    @GetMapping("/api/hotels/{id}/check-availability")
    ApiResponse<Map<String, Boolean>> checkAvailability(@PathVariable("id") Long id);
    
    @PostMapping("/api/hotels/{id}/reserve")
    ApiResponse<Void> reserveRoom(@PathVariable("id") Long id);
}

